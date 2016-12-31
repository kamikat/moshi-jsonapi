package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.Serializable;

public final class HasOne<T extends Resource> extends Relationship<T> implements Serializable {

    private ResourceIdentifier linkedResource;

    public HasOne() { }

    public HasOne(String type, String id) {
        this(new ResourceIdentifier(type, id));
    }

    public HasOne(ResourceIdentifier linkedResource) {
        set(linkedResource);
    }

    @Override
    public T get(Document<?> document) {
        return get(document, null);
    }

    public T get(Document<?> document, T defaultValue) {
        T obj = document.find(linkedResource);
        if (obj == null) {
            return defaultValue;
        } else {
            return obj;
        }
    }

    public ResourceIdentifier get() {
        return linkedResource;
    }

    public void set(ResourceIdentifier identifier) {
        if (identifier == null) {
            linkedResource = null;
        } else if (ResourceIdentifier.class == identifier.getClass()) {
            linkedResource = identifier;
        } else {
            set(identifier.getType(), identifier.getId());
        }
    }

    public void set(String type, String id) {
        set(new ResourceIdentifier(type, id));
    }

    @Override
    public String toString() {
        return "HasOne{" +
                "linkedResource=" + linkedResource +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HasOne<?> hasOne = (HasOne<?>) o;

        return linkedResource != null ? linkedResource.equals(hasOne.linkedResource) : hasOne.linkedResource == null;

    }

    @Override
    public int hashCode() {
        return linkedResource != null ? linkedResource.hashCode() : 0;
    }


    static class Adapter<T extends Resource> extends JsonAdapter<HasOne<T>> {

        JsonAdapter<ResourceIdentifier> resourceIdentifierJsonAdapter;
        JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;

        public Adapter(Moshi moshi) {
            resourceIdentifierJsonAdapter = moshi.adapter(ResourceIdentifier.class);
            jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);
        }

        @Override
        public HasOne<T> fromJson(JsonReader reader) throws IOException {
            HasOne<T> relationship = new HasOne<>();
            reader.beginObject();
            while (reader.hasNext()) {
                final String key = reader.nextName();
                if (reader.peek() == JsonReader.Token.NULL) {
                    reader.skipValue();
                    continue;
                }
                switch (key) {
                    case "data":
                        relationship.set(resourceIdentifierJsonAdapter.fromJson(reader));
                        break;
                    case "meta":
                        relationship.setMeta(jsonBufferJsonAdapter.fromJson(reader));
                        break;
                    case "links":
                        relationship.setLinks(jsonBufferJsonAdapter.fromJson(reader));
                        break;
                    default: {
                        reader.skipValue();
                    }
                    break;
                }
            }
            reader.endObject();
            return relationship;
        }

        @Override
        public void toJson(JsonWriter writer, HasOne<T> value) throws IOException {
            writer.beginObject();
            writer.name("data");
            if (value.linkedResource != null) {
                resourceIdentifierJsonAdapter.toJson(writer, value.linkedResource);
            } else {
                writer.nullValue();
            }
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
    }
}
