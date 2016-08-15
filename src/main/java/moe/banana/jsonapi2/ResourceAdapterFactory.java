package moe.banana.jsonapi2;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import okio.Buffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ResourceAdapterFactory implements JsonAdapter.Factory {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        List<Class<? extends Resource>> types = new ArrayList<>();

        private Builder() { }

        public Builder add(Class<? extends Resource> type) {
            types.add(type);
            return this;
        }

        public ResourceAdapterFactory build() {
            try {
                return new ResourceAdapterFactory(types);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

    }

    private Multimap<String, ResourceTypeInfo> typeNameMap = TreeMultimap.create(
            Ordering.natural(), (l, r) -> l.jsonApi.priority() - r.jsonApi.priority());

    private ResourceAdapterFactory(List<Class<? extends Resource>> types) throws ClassNotFoundException {
        for (Class<? extends Resource> type : types) {
            ResourceTypeInfo<?> info = new ResourceTypeInfo<>(type);
            typeNameMap.put(info.jsonApi.type(), info);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        if (ResourceLinkage.class == type) return new ResourceLinkageAdapter();
        if (Resource.class == type) return new Adapter<>(Resource.class, moshi);
        if (Resource[].class == type) return new ArrayAdapter<>(Resource.class, moshi);
        for (ResourceTypeInfo info : typeNameMap.values()) {
            if (info.type == type) {
                return new Adapter<>(info.type, moshi);
            } else if (info.arrayType == type) {
                return new ArrayAdapter<>(info.type, moshi);
            }
        }
        return null;
    }

    private class Adapter<T extends Resource> extends JsonAdapter<T> {

        public Adapter(Class<T> type, Moshi moshi) {

        }

        @Override
        public T fromJson(JsonReader reader) throws IOException {
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            // TODO de-serialize document or resource
            return null;
        }

        @Override
        public void toJson(JsonWriter writer, T value) throws IOException {
            // TODO serialize document or resource
        }
    }

    private class ArrayAdapter<T extends Resource> extends JsonAdapter<T> {

        public ArrayAdapter(Class<T> componentType, Moshi moshi) {

        }

        @Override
        public T fromJson(JsonReader reader) throws IOException {
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            // TODO de-serialize document with array of resources or array of resources
            return null;
        }

        @Override
        public void toJson(JsonWriter writer, T value) throws IOException {
            // TODO serialize document with array of resources or array of resources
        }
    }
}
