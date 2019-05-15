package moe.banana.jsonapi2;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static moe.banana.jsonapi2.MoshiHelper.*;

class ResourceAdapter<T extends Resource> extends JsonAdapter<T> {

    private final Constructor<T> constructor;

    private static final int TYPE_ATTRIBUTE = 0x01;
    private static final int TYPE_RELATIONSHIP = 0x03;

    private final Map<String, FieldAdapter> bindings = new LinkedHashMap<>();
    private final JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;

    ResourceAdapter(Class<T> type, JsonNameMapping jsonNameMapping, Moshi moshi) {
        this.jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);

        try {
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No default constructor on [" + type + "]", e);
        }

        for (Field field : listFields(type, Resource.class)) {
            int modifiers = field.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
                // skip transient fields and static fields
                continue;
            }
            if (!Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers)) {
                // make private or final fields accessible
                field.setAccessible(true);
            }
            String name = jsonNameMapping.getJsonName(field);
            if (bindings.containsKey(name)) {
                throw new IllegalArgumentException("Duplicated field '" + name + "' in [" + type + "].");
            }
            bindings.put(name, new FieldAdapter<>(field,
                    Relationship.class.isAssignableFrom(Types.getRawType(field.getGenericType())) ? TYPE_RELATIONSHIP: TYPE_ATTRIBUTE,
                    moshi.adapter(field.getGenericType(), AnnotationUtils.jsonAnnotations(field.getAnnotations()))));
        }
    }

    @Override
    public T fromJson(JsonReader reader) throws IOException {
        T resource;
        try {
            resource = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "id":
                    resource.setId(nextNullableString(reader));
                    break;
                case "type":
                    resource.setType(nextNullableString(reader));
                    break;
                case "attributes":
                case "relationships":
                    readFields(reader, resource);
                    break;
                case "meta":
                    resource.setMeta(nextNullableObject(reader, jsonBufferJsonAdapter));
                    break;
                case "links":
                    resource.setLinks(nextNullableObject(reader, jsonBufferJsonAdapter));
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return resource;
    }


    @Override
    public void toJson(JsonWriter writer, T value) throws IOException {
        writer.beginObject();
        writer.name("type").value(value.getType());
        writer.name("id").value(value.getId());
        writeFields(writer, TYPE_ATTRIBUTE, "attributes", value);
        writeFields(writer, TYPE_RELATIONSHIP, "relationships", value);
        writeNullable(writer, jsonBufferJsonAdapter, "meta", value.getMeta());
        writeNullable(writer, jsonBufferJsonAdapter, "links", value.getLinks());
        writer.endObject();
    }

    private void readFields(JsonReader reader, Object resource) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            FieldAdapter fieldAdapter = bindings.get(reader.nextName());
            if (fieldAdapter != null) {
                fieldAdapter.readFrom(reader, resource);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void writeFields(JsonWriter writer, int fieldType, String name, Object value) throws IOException {
        boolean skipFlag = true;
        for (Map.Entry<String, FieldAdapter> entry : bindings.entrySet()) {
            FieldAdapter<?> adapter = entry.getValue();
            if (adapter.fieldType != fieldType) {
                continue;
            }
            if (adapter.get(value) == null && !writer.getSerializeNulls()) {
                // skip write of null values
                continue;
            }
            if (skipFlag) {
                writer.name(name).beginObject();
                skipFlag = false;
            }
            writer.name(entry.getKey());
            adapter.writeTo(writer, value);
        }
        if (!skipFlag) {
            writer.endObject();
        }
    }

    private static List<Field> listFields(Class<?> type, Class<?> baseType) {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = type;
        while (clazz != baseType) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static class FieldAdapter<T> {

        final Field field;
        final JsonAdapter<T> adapter;
        final int fieldType;

        FieldAdapter(Field field, int fieldType, JsonAdapter<T> adapter) {
            this.field = field;
            this.fieldType = fieldType;
            this.adapter = adapter;
        }

        void set(Object target, T value){
            try {
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        T get(Object object) {
            try {
                return (T) field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        void readFrom(JsonReader reader, Object object) throws IOException {
            set(object, nextNullableObject(reader, adapter));
        }

        void writeTo(JsonWriter writer, Object object) throws IOException {
            writeNullableValue(writer, adapter, get(object), false);
        }
    }
}
