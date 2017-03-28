package moe.banana.jsonapi2;

import com.squareup.moshi.*;
import okio.Buffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static moe.banana.jsonapi2.MoshiHelper.nextNullableObject;
import static moe.banana.jsonapi2.MoshiHelper.writeNullable;

public final class ResourceAdapterFactory implements JsonAdapter.Factory {

    private Map<String, Class<?>> typeMap = new HashMap<>();

    private ResourceAdapterFactory(List<Class<? extends Resource>> types) {
        for (Class<? extends Resource> type : types) {
            JsonApi annotation = type.getAnnotation(JsonApi.class);
            String typeName = annotation.type();
            if (typeMap.containsKey(typeName) &&
                    typeMap.get(typeName).getAnnotation(JsonApi.class).priority() < annotation.priority()) {
                continue;
            }
            typeMap.put(typeName, type);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        Class<?> rawType = Types.getRawType(type);
        if (rawType.equals(JsonBuffer.class)) return new JsonBuffer.Adapter();
        if (rawType.equals(HasMany.class)) return new HasMany.Adapter(moshi);
        if (rawType.equals(HasOne.class)) return new HasOne.Adapter(moshi);
        if (rawType.equals(Error.class)) return new Error.Adapter(moshi);
        if (rawType.equals(ResourceIdentifier.class)) return new ResourceIdentifier.Adapter(moshi);
        if (rawType.equals(Resource.class)) return new GenericAdapter(typeMap, moshi);
        if (Document.class.isAssignableFrom(rawType)) {
            if (type instanceof ParameterizedType) {
                Type typeParameter = ((ParameterizedType) type).getActualTypeArguments()[0];
                if (typeParameter instanceof Class<?>) {
                    return new DocumentAdapter((Class<?>) typeParameter, moshi);
                }
            }
            return new DocumentAdapter<>(Resource.class, moshi);
        }
        if (Resource.class.isAssignableFrom(rawType)) return new ResourceAdapter(rawType, moshi);
        return null;
    }

    static class DocumentAdapter<DATA extends ResourceIdentifier> extends JsonAdapter<Document<DATA>> {

        JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;
        JsonAdapter<Error> errorJsonAdapter;
        JsonAdapter<DATA> dataJsonAdapter;
        JsonAdapter<Resource> resourceJsonAdapter;

        public DocumentAdapter(Class<DATA> type, Moshi moshi) {
            jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);
            resourceJsonAdapter = moshi.adapter(Resource.class);
            errorJsonAdapter = moshi.adapter(Error.class);
            dataJsonAdapter = moshi.adapter(type);
        }

        @Override
        public Document<DATA> fromJson(JsonReader reader) throws IOException {
            if (reader.peek() == JsonReader.Token.NULL) {
                return null;
            }
            Document<DATA> document = new ObjectDocument<>();
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "data":
                        if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
                            document = document.asArrayDocument();
                            reader.beginArray();
                            while (reader.hasNext()) {
                                ((ArrayDocument<DATA>) document).add(dataJsonAdapter.fromJson(reader));
                            }
                            reader.endArray();
                        } else if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                            document = document.asObjectDocument();
                            ((ObjectDocument<DATA>) document).set(dataJsonAdapter.fromJson(reader));
                        } else if (reader.peek() == JsonReader.Token.NULL) {
                            reader.nextNull();
                            document = document.asObjectDocument();
                            ((ObjectDocument<DATA>) document).setNull(true);
                        } else {
                            reader.skipValue();
                        }
                        break;
                    case "included":
                        reader.beginArray();
                        while (reader.hasNext()) {
                            document.include(resourceJsonAdapter.fromJson(reader));
                        }
                        reader.endArray();
                        break;
                    case "errors":
                        reader.beginArray();
                        List<Error> errors = document.errors();
                        while (reader.hasNext()) {
                            errors.add(errorJsonAdapter.fromJson(reader));
                        }
                        reader.endArray();
                        break;
                    case "links":
                        document.setLinks(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    case "meta":
                        document.setMeta(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    case "jsonapi":
                        document.setJsonApi(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return document;
        }

        @Override
        public void toJson(JsonWriter writer, Document<DATA> value) throws IOException {
            writer.beginObject();
            if (value instanceof ArrayDocument) {
                writer.name("data");
                writer.beginArray();
                for (DATA resource : ((ArrayDocument<DATA>) value)) {
                    dataJsonAdapter.toJson(writer, resource);
                }
                writer.endArray();
            } else if (value instanceof ObjectDocument) {
                writer.name("data");
                if (((ObjectDocument<DATA>) value).isNull()) {
                    boolean serializeFlag = writer.getSerializeNulls();
                    try {
                        writer.setSerializeNulls(true);
                        writer.nullValue();
                    } finally {
                        writer.setSerializeNulls(serializeFlag);
                    }
                } else if (((ObjectDocument<DATA>) value).get() == null) {
                    writer.nullValue();
                } else {
                    dataJsonAdapter.toJson(writer, ((ObjectDocument<DATA>) value).get());
                }
            }
            if (value.included.size() > 0) {
                writer.name("included");
                writer.beginArray();
                for (Document.ResourceReference resource : value.included) {
                    resourceJsonAdapter.toJson(writer, resource.get());
                }
                writer.endArray();
            }
            if (value.errors.size() > 0) {
                writer.name("error");
                writer.beginArray();
                for (Error err : value.errors) {
                    errorJsonAdapter.toJson(writer, err);
                }
                writer.endArray();
            }
            writeNullable(writer, jsonBufferJsonAdapter, "meta", value.getMeta());
            writeNullable(writer, jsonBufferJsonAdapter, "links", value.getLinks());
            writeNullable(writer, jsonBufferJsonAdapter, "jsonapi", value.getJsonApi());
            writer.endObject();
        }

    }

    private static class GenericAdapter extends JsonAdapter<Resource> {

        Map<String, Class<?>> typeMap;
        Moshi moshi;

        GenericAdapter(Map<String, Class<?>> typeMap, Moshi moshi) {
            this.typeMap = typeMap;
            this.moshi = moshi;
        }

        @Override
        public Resource fromJson(JsonReader reader) throws IOException {
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            String typeName = findTypeOf(buffer);
            JsonAdapter<?> adapter;
            if (typeMap.containsKey(typeName)) {
                adapter = moshi.adapter(typeMap.get(typeName));
            } else if (typeMap.containsKey("default")) {
                adapter = moshi.adapter(typeMap.get("default"));
            } else {
                throw new JsonDataException("Unknown type of resource: " + typeName);
            }
            return (Resource) adapter.fromJson(buffer);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void toJson(JsonWriter writer, Resource value) throws IOException {
            moshi.adapter((Class) value.getClass()).toJson(writer, value);
        }

        private static String findTypeOf(Buffer buffer) throws IOException {
            Buffer forked = new Buffer();
            buffer.copyTo(forked, 0, buffer.size());
            JsonReader reader = JsonReader.of(forked);
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "type":
                        return reader.nextString();
                    default:
                        reader.skipValue();
                }
            }
            return null;
        }
    }

    public static class Builder {

        List<Class<? extends Resource>> types = new ArrayList<>();

        private Builder() { }

        @SafeVarargs
        public final Builder add(Class<? extends Resource>... type) {
            types.addAll(Arrays.asList(type));
            return this;
        }

        public final ResourceAdapterFactory build() {
            return new ResourceAdapterFactory(types);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
