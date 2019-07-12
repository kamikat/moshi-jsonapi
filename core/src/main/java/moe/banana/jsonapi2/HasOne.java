package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;

import static moe.banana.jsonapi2.MoshiHelper.nextNullableObject;
import static moe.banana.jsonapi2.MoshiHelper.writeNullable;

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
    @Nullable
    public T get(@NotNull Document document) {
        return get(document, null);
    }

    @Nullable
    public T get(@NotNull Document document, T defaultValue) {
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
                switch (reader.nextName()) {
                    case "data":
                        relationship.set(nextNullableObject(reader, resourceIdentifierJsonAdapter));
                        break;
                    case "meta":
                        relationship.setMeta(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    case "links":
                        relationship.setLinks(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return relationship;
        }

        @Override
        public void toJson(JsonWriter writer, HasOne<T> value) throws IOException {
            writer.beginObject();
            writeNullable(writer, resourceIdentifierJsonAdapter, "data", value.linkedResource, true);
            writeNullable(writer, jsonBufferJsonAdapter, "meta", value.getMeta());
            writeNullable(writer, jsonBufferJsonAdapter, "links", value.getLinks());
            writer.endObject();
        }
    }
}
