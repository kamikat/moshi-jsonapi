package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

class ResourceLinkageAdapter extends JsonAdapter<ResourceLinkage> {

    @Override
    public ResourceLinkage fromJson(JsonReader reader) throws IOException {
        String type = null, id = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String _name = reader.nextName();
            switch (_name) {
                case "type": {
                    type = reader.nextString();
                    break;
                }
                case "id": {
                    id = reader.nextString();
                    break;
                }
                default: {
                    reader.skipValue();
                }
            }
        }
        reader.endObject();
        return ResourceLinkage.of(type, id);
    }

    @Override
    public void toJson(JsonWriter writer, ResourceLinkage value) throws IOException {
        writer.beginObject();
        writer.name("type").value(value.getType());
        writer.name("id").value(value.getId());
        writer.endObject();
    }
}
