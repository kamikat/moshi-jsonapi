package moe.banana.jsonapi;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * resource json adapter
 */
final class ResourceJsonAdapter extends JsonAdapter<Resource> {

    private final JsonAdapter<Object> mDefaultAdapter;
    private final JsonAdapter<String> mTypeAdapter;
    private final JsonAdapter<String> mIdAdapter;
    private final JsonAdapter<Map<String, Relationship>> mRelationshipsAdapter;
    private final JsonAdapter<Links> mLinksAdapter;

    private final Map<String, JsonAdapter<Object>> mNameAdapterMap;
    private final Map<Type, JsonAdapter<Object>> mTypeAdapterMap;

    public ResourceJsonAdapter(LinkedHashMap<Type, String> types, Moshi moshi) {
        mDefaultAdapter = moshi.adapter(Object.class);
        mTypeAdapter = moshi.adapter(String.class);
        mIdAdapter = moshi.adapter(String.class);
        mRelationshipsAdapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Relationship.class));
        mLinksAdapter = moshi.adapter(Links.class);
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
        reader.beginObject();
        String type = null;
        String id = null;
        Object attributes = null;
        Map<String, Relationship> relationships = null;
        Links links = null;
        Object meta = null;
        JsonAdapter attributesAdapter = null;
        while (reader.hasNext()) {
            String _name = reader.nextName();
            if (reader.peek() == JsonReader.Token.NULL) {
                reader.skipValue();
                continue;
            }
            switch (_name) {
                case "type": {
                    type = mTypeAdapter.fromJson(reader);
                    attributesAdapter = mNameAdapterMap.get(type);
                    if (attributes != null) {
                        attributes = attributesAdapter.fromJson(mDefaultAdapter.toJson(attributes));
                    }
                    break;
                }
                case "id": {
                    id = mIdAdapter.fromJson(reader);
                    break;
                }
                case "attributes": {
                    if (attributesAdapter == null) {
                        attributes = mDefaultAdapter.fromJson(reader);
                    } else {
                        attributes = attributesAdapter.fromJson(reader);
                    }
                    break;
                }
                case "relationships": {
                    relationships = mRelationshipsAdapter.fromJson(reader);
                    break;
                }
                case "links": {
                    links = mLinksAdapter.fromJson(reader);
                    break;
                }
                case "meta": {
                    meta = mDefaultAdapter.fromJson(reader);
                    break;
                }
                default: {
                    reader.skipValue();
                }
            }
        }
        reader.endObject();
        return new AutoValue_Resource(meta, type, id, attributes, relationships, links);
    }

    @Override
    public void toJson(JsonWriter writer, Resource value) throws IOException {
        writer.beginObject();
        if (value.type() != null) {
            writer.name("type");
            mTypeAdapter.toJson(writer, value.type());
        }
        if (value.id() != null) {
            writer.name("id");
            mIdAdapter.toJson(writer, value.id());
        }
        Object attributes = value.attributes();
        if (attributes != null) {
            writer.name("attributes");
            JsonAdapter<Object> attributesAdapter = null;
            Class<?> clazz = attributes.getClass();
            while (attributesAdapter == null && clazz != Object.class) {
                attributesAdapter = mTypeAdapterMap.get(clazz);
                clazz = clazz.getSuperclass();
            }
            if (attributesAdapter == null) {
                attributesAdapter = mNameAdapterMap.get(value.type());
            }
            if (attributesAdapter == null) {
                throw new JsonDataException("Cannot found attributes JsonAdapter for resource type [" + value.type() + "]");
            } else {
                attributesAdapter.toJson(writer, value.attributes());
            }
        }
        if (value.relationships() != null) {
            writer.name("relationships");
            mRelationshipsAdapter.toJson(writer, value.relationships());
        }
        if (value.links() != null) {
            writer.name("links");
            mLinksAdapter.toJson(writer, value.links());
        }
        if (value.meta() != null) {
            writer.name("meta");
            mDefaultAdapter.toJson(writer, value.meta());
        }
        writer.endObject();
    }

}
