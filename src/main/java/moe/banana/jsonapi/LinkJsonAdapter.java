package moe.banana.jsonapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

final class LinkJsonAdapter extends JsonAdapter<Link> {

    private final JsonAdapter<Link> mJsonAdapter;

    public LinkJsonAdapter(Moshi moshi) {
        mJsonAdapter = moshi.adapter(Link.class);
    }

    @Override
    public @Implicit Link fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING:
                return new AutoValue_Link(null, reader.nextString());
            case NULL:
                return reader.nextNull();
            case BEGIN_OBJECT:
                return mJsonAdapter.fromJson(reader);
            default:
                throw new JsonDataException();
        }
    }

    @Override
    public void toJson(JsonWriter writer, @Implicit Link value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value.meta() == null) {
            writer.value(value.href());
        } else {
            mJsonAdapter.toJson(writer, value);
        }
    }

}
