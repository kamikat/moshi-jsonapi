package moe.banana.jsonapi2;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

class ResourceAdapter<T extends Resource> extends JsonAdapter<T> {

    private final Class<T> type;

    private static final int TYPE_ATTRIBUTE = 0x01;
    private static final int TYPE_RELATIONSHIP = 0x03;

    private final Map<String, FieldAdapter> bindings = new LinkedHashMap<>();
    private final JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;

    ResourceAdapter(Class<T> type, Moshi moshi) {
        this.jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);
        this.type = type;

        for (Field field : listFields(type, Resource.class)) {
            int modifiers = field.getModifiers();
            if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
                // skip transient fields and static fields
                continue;
            }
            if (!Modifier.isPublic(modifiers)) {
                // make private fields accessible
                field.setAccessible(true);
            }
            String name = field.getName();
            Json json = field.getAnnotation(Json.class);
            if (json != null) {
                name = json.name();
            }
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
            resource = type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();
            if (reader.peek() == JsonReader.Token.NULL) {
                reader.skipValue();
                continue;
            }
            switch (key) {
                case "id":
                    resource.setId(reader.nextString());
                    break;
                case "type":
                    resource.setType(reader.nextString());
                    break;
                case "attributes":
                    readFields(reader, resource);
                    break;
                case "relationships":
                    readFields(reader, resource);
                    break;
                case "meta":
                    resource.setMeta(jsonBufferJsonAdapter.fromJson(reader));
                    break;
                case "links":
                    resource.setLinks(jsonBufferJsonAdapter.fromJson(reader));
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
        writeFields(writer, value, "attributes", TYPE_ATTRIBUTE);
        writeFields(writer, value, "relationships", TYPE_RELATIONSHIP);
        if (value.getMeta() != null) {
            writer.name("meta");
            jsonBufferJsonAdapter.toJson(writer, value.getMeta());
        }
        if (value.getLinks() != null) {
            writer.name("links");
            jsonBufferJsonAdapter.toJson(writer, value.getLinks());
        }
        writer.endObject();
    }

    private void readFields(JsonReader reader, Object resource) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();
            if (reader.peek() == JsonReader.Token.NULL) {
                reader.skipValue();
                continue;
            }
            if (bindings.containsKey(key)) {
                bindings.get(key).readFrom(reader, resource);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void writeFields(JsonWriter writer, Object value, String name, int fieldType) throws IOException {
        boolean skipFlag = true;
        for (Map.Entry<String, FieldAdapter> entry : bindings.entrySet()) {
            FieldAdapter<?> adapter = entry.getValue();
            if (adapter.fieldType != fieldType) {
                continue;
            }
            if (adapter.get(value) == null) {
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
            set(object, adapter.fromJson(reader));
        }

        void writeTo(JsonWriter writer, Object object) throws IOException {
            adapter.toJson(writer, get(object));
        }
    }
}
