package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * resource json adapter
 */
final class ResourceJsonAdapter extends JsonAdapter<Resource> {

    private final JsonAdapter<Object> mObjectJsonAdapter;
    private final JsonAdapter<PlainResource> mPlainResourceJsonAdapter;
    private final Map<String, JsonAdapter<Object>> mNameAdapterMap;
    private final Map<Type, JsonAdapter<Object>> mTypeAdapterMap;

    public ResourceJsonAdapter(LinkedHashMap<Type, String> types, Moshi moshi) {
        mObjectJsonAdapter = moshi.adapter(Object.class);
        mPlainResourceJsonAdapter = moshi.adapter(PlainResource.class);
        mNameAdapterMap = new HashMap<>(types.size());
        mTypeAdapterMap = new HashMap<>(types.size());
        for (Map.Entry<Type, String> entry : types.entrySet()) {
            try {
                JsonAdapter<Object> adapter = moshi.adapter(entry.getKey());
                mNameAdapterMap.put(entry.getValue(), adapter);
                mTypeAdapterMap.put(entry.getKey(), adapter);
            } catch (Exception e) {
                throw new AssertionError("Cannot find adapter of [" + entry.getKey() + "], did you forget add adapter to moshi builder?", e);
            }
        }
    }

    @Override
    public Resource fromJson(JsonReader reader) throws IOException {
        PlainResource json = mPlainResourceJsonAdapter.fromJson(reader);
        JsonAdapter attributesAdapter;
        Object attributes;
        attributesAdapter = mNameAdapterMap.get(json.type());
        if (attributesAdapter == null) {
            throw new JsonDataException("Cannot found attributes JsonAdapter for resource type [" + json.type() + "]");
        } else {
            attributes = attributesAdapter.fromJson(mObjectJsonAdapter.toJson(json.attributes()));
        }
        return new AutoValue_Resource(json.meta(), json.type(), json.id(), attributes, json.relationships(), json.links());
    }

    @Override
    public void toJson(JsonWriter writer, Resource value) throws IOException {
        String type = value.type(), id = value.id();
        Links links = value.links();
        Map<String, Relationship> relationships = value.relationships();
        Object meta = value.meta();
        Object attributes = value.attributes();
        JsonAdapter<Object> attributesAdapter = null;
        if (attributes != null) {
            Class<?> clazz = attributes.getClass();
            while (attributesAdapter == null && clazz != Object.class) {
                attributesAdapter = mTypeAdapterMap.get(clazz);
                clazz = clazz.getSuperclass();
            }
        }
        if (attributesAdapter == null) {
            attributesAdapter = mNameAdapterMap.get(type);
        }
        if (attributesAdapter == null) {
            throw new JsonDataException("Cannot found attributes JsonAdapter for resource type [" + type + "]");
        } else {
            attributes = mObjectJsonAdapter.fromJson(attributesAdapter.toJson(value.attributes()));
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
