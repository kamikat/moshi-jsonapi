package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;

import static moe.banana.jsonapi2.MoshiHelper.*;

public class ResourceIdentifier implements Serializable {

    @Nullable private Document document;
    @Nullable private String type;
    @Nullable private String id;
    @Nullable private JsonBuffer meta;

    public ResourceIdentifier() {
        this(null, null);
    }

    public ResourceIdentifier(ResourceIdentifier identifier) {
        this(identifier.getType(), identifier.getId());
    }

    public ResourceIdentifier(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Deprecated
    public Document getContext() {
        return getDocument();
    }

    @Deprecated
    public void setContext(Document document) {
        setDocument(document);
    }

    @Nullable
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public JsonBuffer getMeta() {
        return meta;
    }

    public void setMeta(JsonBuffer meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(o.getClass())) return false;

        ResourceIdentifier that = (ResourceIdentifier) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public static class Adapter extends JsonAdapter<ResourceIdentifier> {

        JsonAdapter<JsonBuffer> jsonBufferJsonAdapter;

        public Adapter(Moshi moshi) {
            jsonBufferJsonAdapter = moshi.adapter(JsonBuffer.class);
        }

        @Override
        public ResourceIdentifier fromJson(JsonReader reader) throws IOException {
            ResourceIdentifier object = new ResourceIdentifier();
            reader.beginObject();
            while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "id":
                        object.setId(nextNullableString(reader));
                        break;
                    case "type":
                        object.setType(nextNullableString(reader));
                        break;
                    case "meta":
                        object.setMeta(nextNullableObject(reader, jsonBufferJsonAdapter));
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return object;
        }

        @Override
        public void toJson(JsonWriter writer, ResourceIdentifier value) throws IOException {
            writer.beginObject();
            writer.name("type").value(value.getType());
            writer.name("id").value(value.getId());
            writeNullable(writer, jsonBufferJsonAdapter, "meta", value.getMeta());
            writer.endObject();
        }
    }
}
