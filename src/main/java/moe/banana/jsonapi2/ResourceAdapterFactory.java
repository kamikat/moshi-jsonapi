package moe.banana.jsonapi2;

import com.squareup.moshi.*;
import okio.Buffer;

import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public final class ResourceAdapterFactory implements JsonAdapter.Factory {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        List<Class<? extends Resource>> types = new ArrayList<>();
        boolean permissive = true;

        private Builder() {
            add(Resource.UnresolvedResource.class);
        }

        public Builder add(Class<? extends Resource> type) {
            types.add(type);
            return this;
        }

        /**
         * permissive mode is enabled by default
         * @return the builder
         */
        @Deprecated
        public Builder enablePermissive() {
            return this;
        }

        public Builder strict() {
            permissive = false;
            return this;
        }

        public ResourceAdapterFactory build() {
            return new ResourceAdapterFactory(this);
        }
    }

    private LinkedHashMap<String, TreeSet<ResourceTypeInfo>> typeNameMap = new LinkedHashMap<>();

    private Map<Class<?>, JsonAdapter<?>> adapterMap = new HashMap<>();

    private boolean isPermissive;

    private ResourceAdapterFactory(Builder builder) {
        for (Class<? extends Resource> type : builder.types) {
            ResourceTypeInfo<?> info = new ResourceTypeInfo<>(type);
            String key = info.jsonApi.type();
            if (!typeNameMap.containsKey(key)) {
                typeNameMap.put(key, new TreeSet<>(new Comparator<ResourceTypeInfo>() {
                    @Override
                    public int compare(ResourceTypeInfo l, ResourceTypeInfo r) {
                        return l.jsonApi.priority() - r.jsonApi.priority();
                    }
                }));
            }
            typeNameMap.get(key).add(info);
        }
        isPermissive = builder.permissive;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        Class<?> rawType = Types.getRawType(type);
        if (rawType.equals(ResourceLinkage.class)) return new ResourceLinkageAdapter();
        if (rawType.equals(Resource.class)) return new Adapter<>(Resource.class, moshi);
        if (rawType.equals(Resource[].class)) return new ArrayAdapter<>(Resource.class, moshi);
        for (TreeSet<ResourceTypeInfo> set : typeNameMap.values()) {
            for (ResourceTypeInfo info : set) {
                if (rawType.equals(info.type)) {
                    return new Adapter<>(info.type, moshi);
                } else if (rawType.equals(info.arrayType)) {
                    return new ArrayAdapter<>(info.type, moshi);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> JsonAdapter<T> findAdapter(Class<T> type, Moshi moshi) {
        synchronized (this) {
            if (!adapterMap.containsKey(type)) {
                adapterMap.put(type, new Resource.Adapter<>(type, moshi));
            }
            return (JsonAdapter<T>) adapterMap.get(type);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> T polymorphicFromJson(JsonReader reader, Moshi moshi) throws IOException {
        Buffer buffer = new Buffer();
        MoshiHelper.dump(reader, buffer);
        String typeName = findTypeOf(buffer);
        JsonAdapter adapter;
        if (typeNameMap.containsKey(typeName)) {
            adapter = findAdapter(typeNameMap.get(typeName).iterator().next().type, moshi);
        } else if (isPermissive) {
            adapter = findAdapter(typeNameMap.get(Resource.typeNameOf(Resource.UnresolvedResource.class)).iterator().next().type, moshi);
        } else {
            throw new JsonDataException("Unknown type of resource: " + typeName);
        }
        return (T) adapter.fromJson(buffer);
    }

    @SuppressWarnings("unchecked")
    private void polymorphicToJson(JsonWriter writer, Resource res, Moshi moshi) throws IOException {
        if (!typeNameMap.containsKey(res._type)) {
            throw new JsonDataException("Invalid type argument: " + res._type + ", types should be added to resource adapter factory before use them.");
        }
        JsonAdapter adapter = findAdapter(typeNameMap.get(res._type).iterator().next().type, moshi);
        adapter.toJson(writer, res);
    }

    private class Adapter<T extends Resource> extends JsonAdapter<T> {

        private final Class<T> type;
        private final Moshi moshi;

        Adapter(Class<T> type, Moshi moshi) {
            this.type = type;
            this.moshi = moshi;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T fromJson(JsonReader reader) throws IOException {
            try {
                reader.peek();
            } catch (EOFException eof) {
                return null;
            }
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            if (isDocument(buffer)) {
                Document document = new Document();
                reader = MoshiHelper.copyOf(buffer);
                reader.beginObject();
                T resource = null;
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.skipValue();
                        continue;
                    }
                    switch (name) {
                        case "data": {
                            resource = polymorphicFromJson(reader, moshi);
                            document.addData(resource);
                        } break;
                        case "included": {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                document.addInclude(polymorphicFromJson(reader, moshi));
                            }
                            reader.endArray();
                        } break;
                        default: {
                            reader.skipValue();
                        } break;
                    }
                }
                return resource;
            } else {
                return polymorphicFromJson(MoshiHelper.copyOf(buffer), moshi);
            }
        }

        @Override
        public void toJson(JsonWriter writer, T value) throws IOException {
            if (value._doc != null) {
                writer.beginObject();
                writer.name("data");
            }
            if (Resource.class == type) {
                polymorphicToJson(writer, value, moshi);
            } else {
                JsonAdapter<T> adapter = findAdapter(type, moshi);
                adapter.toJson(writer, value);
            }
            if (value._doc != null) {
                if (value._doc.included.size() > 0) {
                    writer.name("included");
                    writer.beginArray();
                    for (Resource res : value._doc.included) {
                        polymorphicToJson(writer, res, moshi);
                    }
                    writer.endArray();
                }
                writer.endObject();
            }
        }
    }

    private class ArrayAdapter<T extends Resource> extends JsonAdapter<T[]> {

        private final Class<T> componentType;
        private final Moshi moshi;

        ArrayAdapter(Class<T> componentType, Moshi moshi) {
            this.componentType = componentType;
            this.moshi = moshi;
        }

        @Override
        @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
        public T[] fromJson(JsonReader reader) throws IOException {
            try {
                reader.peek();
            } catch (EOFException eof) {
                return null;
            }
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            if (isDocument(buffer)) {
                Document document = new Document();
                reader = MoshiHelper.copyOf(buffer);
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (reader.peek() == JsonReader.Token.NULL) {
                        reader.skipValue();
                        continue;
                    }
                    switch (name) {
                        case "data": {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                document.addData(polymorphicFromJson(reader, moshi));
                            }
                            reader.endArray();
                        } break;
                        case "included": {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                document.addInclude(polymorphicFromJson(reader, moshi));
                            }
                            reader.endArray();
                        } break;
                        default: {
                            reader.skipValue();
                        } break;
                    }
                }
                return document.data.toArray((T[]) Array.newInstance(componentType, document.data.size()));
            } else {
                reader = MoshiHelper.copyOf(buffer);
                List<T> list = new ArrayList<>();
                reader.beginArray();
                while (reader.hasNext()) {
                    list.add((T) polymorphicFromJson(reader, moshi));
                }
                reader.endArray();
                return list.toArray((T[]) Array.newInstance(componentType, list.size()));
            }
        }

        @Override
        public void toJson(JsonWriter writer, T[] values) throws IOException {
            boolean isDocument = values.length == 0 || values[0]._doc != null;
            Set<Resource> included = null;
            if (isDocument) {
                writer.beginObject();
                writer.name("data");
                included = new LinkedHashSet<>();
            }
            writer.beginArray();
            if (Resource.class == componentType) {
                for (T value : values) {
                    if (value._doc != null && isDocument) {
                        included.addAll(value._doc.included);
                    }
                    polymorphicToJson(writer, value, moshi);
                }
            } else {
                for (T value : values) {
                    if (value._doc != null && isDocument) {
                        included.addAll(value._doc.included);
                    }
                    JsonAdapter<T> adapter = findAdapter(componentType, moshi);
                    adapter.toJson(writer, value);
                }
            }
            writer.endArray();
            if (isDocument) {
                if (included.size() > 0) {
                    writer.name("included");
                    writer.beginArray();
                    for (Resource res : included) {
                        polymorphicToJson(writer, res, moshi);
                    }
                    writer.endArray();
                }
                writer.endObject();
            }
        }
    }

    private static boolean isDocument(Buffer buffer) throws IOException {
        JsonReader reader = MoshiHelper.copyOf(buffer);
        if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
            return false;
        }
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            reader.skipValue();
            switch (name) {
                case "data":
                case "included":
                case "errors":
                    return true;
                case "attributes":
                case "relationships":
                case "type":
                case "id":
                    return false;
            }
        }
        throw new JsonDataException("Invalid JSON API document/resource object.");
    }

    private static String findTypeOf(Buffer buffer) throws IOException {
        JsonReader reader = MoshiHelper.copyOf(buffer);
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
