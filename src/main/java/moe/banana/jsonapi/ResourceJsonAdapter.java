package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * resource json adapter
 */
public final class ResourceJsonAdapter extends JsonAdapter<Resource> {

    public static class Factory implements JsonAdapter.Factory {

        static Map<String, Type> processAttributesAnnotation(Class<?>[] classes) {
            Map<String, Type> types = new HashMap<String, Type>(classes.length);
            for (Class<?> cls : classes) {
                AttributesObject attributes = cls.getAnnotation(AttributesObject.class);
                if (attributes == null) {
                    throw new AssertionError("ResourceJsonAdapter requires class with @AttributesObject annotation.");
                }
                types.put(attributes.type(), cls);
            }
            return types;
        }

        Class<?>[] mClasses;

        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            JsonAdapter<?> plainAdapter = AutoValue_ResourceJsonAdapter_PlainResource.typeAdapterFactory().create(type, annotations, moshi);
            if (plainAdapter != null) {
                return plainAdapter;
            }
            if (type == Resource.class) {
                return new ResourceJsonAdapter(processAttributesAnnotation(mClasses), moshi);
            }
            return null;
        }

        public Factory(Class<?>... classes) {
            mClasses = classes;
        }

    }

    private final JsonAdapter<Object> mObjectJsonAdapter;
    private final Map<String, JsonAdapter<Object>> mAttrAdapterMap;
    private final Map<String, JsonAdapter<Object>> mMetaAdapterMap;
    private final JsonAdapter<PlainResource> mPlainResourceJsonAdapter;

    public ResourceJsonAdapter(Map<String, Type> types, Map<String, Type> metaTypes, Moshi moshi) {
        mObjectJsonAdapter = moshi.adapter(Object.class);
        mPlainResourceJsonAdapter = moshi.adapter(PlainResource.class);
        mAttrAdapterMap = new HashMap<String, JsonAdapter<Object>>(types.size());
        for (String key : types.keySet()) {
            mAttrAdapterMap.put(key, moshi.adapter(types.get(key)));
        }
        if (metaTypes == null) {
            mMetaAdapterMap = null;
        } else {
            mMetaAdapterMap = new HashMap<String, JsonAdapter<Object>>(metaTypes.size());
            for (String key : metaTypes.keySet()) {
                mMetaAdapterMap.put(key, moshi.adapter(metaTypes.get(key)));
            }
        }
    }

    public ResourceJsonAdapter(Map<String, Type> types, Moshi moshi) {
        this(types, null, moshi);
    }

    @Override
    public Resource fromJson(JsonReader reader) throws IOException {
        PlainResource json = mPlainResourceJsonAdapter.fromJson(reader);
        JsonAdapter metaAdapter, attributesAdapter;
        Object meta, attributes;
        attributesAdapter = mAttrAdapterMap.get(json.type());
        if (attributesAdapter == null) {
            throw new JsonDataException("Cannot found attributes JsonAdapter for resource type [" + json.type() + "]");
        } else {
            attributes = attributesAdapter.fromJson(mObjectJsonAdapter.toJson(json.attributes()));
        }
        if (mMetaAdapterMap == null) {
            meta = json.meta();
        } else {
            metaAdapter = mMetaAdapterMap.get(json.type());
            if (metaAdapter == null) {
                throw new JsonDataException("Cannot found meta JsonAdapter for resource type [" + json.type() + "]");
            } else {
                meta = metaAdapter.fromJson(mObjectJsonAdapter.toJson(json.meta()));
            }
        }
        return new AutoValue_Resource(meta, json.type(), json.id(), attributes, json.relationships(), json.links());
    }

    @Override
    public void toJson(JsonWriter writer, Resource value) throws IOException {
        String type = value.type(), id = value.id();
        Links links = value.links();
        Map<String, Relationship> relationships = value.relationships();
        Object attributes;
        Object meta;
        JsonAdapter<Object> metaAdapter, attributesAdapter;
        attributesAdapter = mAttrAdapterMap.get(type);
        if (attributesAdapter == null) {
            throw new JsonDataException("Cannot found attributes JsonAdapter for resource type [" + type + "]");
        } else {
            attributes = mObjectJsonAdapter.fromJson(attributesAdapter.toJson(value.attributes()));
        }
        if (mMetaAdapterMap == null) {
            meta = value.meta();
        } else {
            metaAdapter = mMetaAdapterMap.get(type);
            if (metaAdapter == null) {
                throw new JsonDataException("Cannot found meta JsonAdapter for resource type [" + type + "]");
            } else {
                meta = mObjectJsonAdapter.fromJson(attributesAdapter.toJson(value.meta()));
            }
        }
        mPlainResourceJsonAdapter.toJson(writer, new AutoValue_ResourceJsonAdapter_PlainResource(type, id, attributes, relationships, links, meta));
    }

    /**
     * plain resource object
     */
    @AutoValue
    static abstract class PlainResource {
        @Nullable abstract String type();
        @Nullable abstract String id();
        @Nullable abstract Object attributes();
        @Nullable abstract Map<String, Relationship> relationships();
        @Nullable abstract Links links();
        @Nullable abstract Object meta();
    }

}
