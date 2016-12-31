package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class HasMany<T extends Resource> extends Relationship<List<T>> implements Iterable<ResourceIdentifier>, Serializable {

    private final List<ResourceIdentifier> linkedResources = new ArrayList<>();

    public HasMany() { }

    public HasMany(ResourceIdentifier... resources) {
        for (ResourceIdentifier resource : resources) {
            add(resource);
        }
    }

    @Override
    public List<T> get(Document<?> document) {
        return get(document, null);
    }

    public List<T> get(Document<?> document, T defaultValue) {
        List<T> collector = new ArrayList<>(linkedResources.size());
        for (ResourceIdentifier resourceId : linkedResources) {
            T obj = document.find(resourceId);
            collector.add(obj == null ? defaultValue : obj);
        }
        return collector;
    }

    public ResourceIdentifier get(int position) {
        return linkedResources.get(position);
    }

    public List<ResourceIdentifier> get() {
        return Arrays.asList(linkedResources.toArray(new ResourceIdentifier[linkedResources.size()]));
    }

    @Override
    public Iterator<ResourceIdentifier> iterator() {
        return linkedResources.iterator();
    }

    public boolean add(ResourceIdentifier identifier) {
        if (identifier == null) {
            return false;
        } else if (identifier.getClass() == ResourceIdentifier.class) {
            return linkedResources.add(identifier);
        } else {
            return add(identifier.getType(), identifier.getId());
        }
    }

    public boolean add(String type, String id) {
        return add(new ResourceIdentifier(type, id));
    }

    public boolean remove(ResourceIdentifier identifier) {
        return remove(identifier.getType(), identifier.getId());
    }

    public boolean remove(String type, String id) {
        return linkedResources.remove(new ResourceIdentifier(type, id));
    }

    public int size() {
        return linkedResources.size();
    }

    @Override
    public String toString() {
        return "HasMany{" +
                "linkedResources=" + linkedResources +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HasMany<?> hasMany = (HasMany<?>) o;

        return linkedResources.equals(hasMany.linkedResources);

    }

    @Override
    public int hashCode() {
        return linkedResources.hashCode();
    }

    static class Adapter<T extends Resource> extends JsonAdapter<HasMany<T>> {

        JsonAdapter<ResourceIdentifier> resourceIdentifierJsonAdapter;
        JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;

        public Adapter(Moshi moshi) {
            resourceIdentifierJsonAdapter = moshi.adapter(ResourceIdentifier.class);
            jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);
        }

        @Override
        public HasMany<T> fromJson(JsonReader reader) throws IOException {
            HasMany<T> relationship = new HasMany<>();
            reader.beginObject();
            while (reader.hasNext()) {
                final String key = reader.nextName();
                if (reader.peek() == JsonReader.Token.NULL) {
                    reader.skipValue();
                    continue;
                }
                switch (key) {
                    case "data":
                        reader.beginArray();
                        while (reader.hasNext()) {
                            relationship.add(resourceIdentifierJsonAdapter.fromJson(reader));
                        }
                        reader.endArray();
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
        public void toJson(JsonWriter writer, HasMany<T> value) throws IOException {
            writer.beginObject();
            writer.name("data");
            writer.beginArray();
            for (ResourceIdentifier resource : value.linkedResources) {
                resourceIdentifierJsonAdapter.toJson(writer, resource);
            }
            writer.endArray();
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
