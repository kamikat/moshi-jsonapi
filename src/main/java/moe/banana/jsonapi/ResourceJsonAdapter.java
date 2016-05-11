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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * resource json adapter
 */
final class ResourceJsonAdapter extends JsonAdapter<Resource> {

    private final JsonAdapter<Object> mObjectJsonAdapter;
    private final Map<String, JsonAdapter<Object>> mAttrAdapterMap;
    private final JsonAdapter<PlainResource> mPlainResourceJsonAdapter;

    public ResourceJsonAdapter(Map<String, Type> types, Moshi moshi) {
        mObjectJsonAdapter = moshi.adapter(Object.class);
        mPlainResourceJsonAdapter = moshi.adapter(PlainResource.class);
        mAttrAdapterMap = new HashMap<>(types.size());
        for (String key : types.keySet()) {
            Type type = types.get(key);
            try {
                mAttrAdapterMap.put(key, moshi.adapter(type));
            } catch (Exception e) {
                throw new AssertionError("Cannot find adapter of [" + type + "], did you forget add adapter to moshi builder?");
            }
        }
    }

    @Override
    public Resource fromJson(JsonReader reader) throws IOException {
        PlainResource json = mPlainResourceJsonAdapter.fromJson(reader);
        JsonAdapter attributesAdapter;
        Object attributes;
        attributesAdapter = mAttrAdapterMap.get(json.type());
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
        Object attributes;
        JsonAdapter<Object> attributesAdapter;
        attributesAdapter = mAttrAdapterMap.get(type);
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
