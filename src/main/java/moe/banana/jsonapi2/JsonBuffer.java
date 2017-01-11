package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import okio.Buffer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Buffer JSON result as byte[] for lazy bind
 */
public class JsonBuffer<T> implements Serializable {

    private byte[] buffer;

    private JsonBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public <R extends T> R get(JsonAdapter<R> adapter) {
        try {
            Buffer buffer = new Buffer();
            buffer.write(this.buffer);
            return adapter.fromJson(buffer);
        } catch (IOException e) {
            throw new RuntimeException("JsonBuffer failed to deserialize value with [" + adapter.getClass() + "]", e);
        }
    }

    public static <T> JsonBuffer<T> create(JsonAdapter<T> adapter, T value) {
        try {
            Buffer buffer = new Buffer();
            adapter.toJson(buffer, value);
            return new JsonBuffer<>(buffer.readByteArray());
        } catch (IOException e) {
            throw new RuntimeException("JsonBuffer failed to serialize value with [" + adapter.getClass() + "]", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonBuffer<?> that = (JsonBuffer<?>) o;

        return Arrays.equals(buffer, that.buffer);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(buffer);
    }

    public static class Adapter<T> extends JsonAdapter<JsonBuffer<T>> {

        @Override
        public JsonBuffer<T> fromJson(JsonReader reader) throws IOException {
            Buffer buffer = new Buffer();
            MoshiHelper.dump(reader, buffer);
            return new JsonBuffer<>(buffer.readByteArray());
        }

        @Override
        public void toJson(JsonWriter writer, JsonBuffer<T> value) throws IOException {
            Buffer buffer = new Buffer();
            buffer.write(value.buffer);
            MoshiHelper.dump(buffer, writer);
        }
    }
}
