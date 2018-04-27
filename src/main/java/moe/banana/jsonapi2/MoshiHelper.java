package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import okio.BufferedSink;
import okio.BufferedSource;

import java.io.IOException;

public final class MoshiHelper {

    public static void dump(BufferedSource source, JsonWriter writer) throws IOException {
        dump(JsonReader.of(source), writer);
    }

    public static void dump(JsonReader reader, BufferedSink sink) throws IOException {
        dump(reader, JsonWriter.of(sink));
    }

    public static void dump(JsonReader reader, JsonWriter writer) throws IOException {
        int nested = 0;
        boolean nullFlag = writer.getSerializeNulls();
        writer.setSerializeNulls(true);
        try {
            while (reader.peek() != JsonReader.Token.END_DOCUMENT) {
                switch (reader.peek()) {
                    case BEGIN_ARRAY:
                        nested++;
                        reader.beginArray();
                        writer.beginArray();
                        break;
                    case END_ARRAY:
                        reader.endArray();
                        writer.endArray();
                        if (0 == --nested) return;
                        break;
                    case BEGIN_OBJECT:
                        nested++;
                        reader.beginObject();
                        writer.beginObject();
                        break;
                    case END_OBJECT:
                        reader.endObject();
                        writer.endObject();
                        if (0 == --nested) return;
                        break;
                    case NAME:
                        writer.name(reader.nextName());
                        break;
                    case NUMBER:
                        try {
                            writer.value(reader.nextLong());
                        } catch (Exception ignored) {
                            writer.value(reader.nextDouble());
                        }
                        break;
                    case BOOLEAN:
                        writer.value(reader.nextBoolean());
                        break;
                    case STRING:
                        writer.value(reader.nextString());
                        break;
                    case NULL:
                        reader.nextNull();
                        writer.nullValue();
                        break;
                }
            }
        } finally {
            writer.setSerializeNulls(nullFlag);
        }
    }

    public static String nextNullableString(JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.skipValue();
            return null;
        } else {
            return reader.nextString();
        }
    }

    public static <T> T nextNullableObject(JsonReader reader, JsonAdapter<T> adapter) throws IOException {
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.skipValue();
            return null;
        } else {
            return adapter.fromJson(reader);
        }
    }

    public static <T> void writeNullable(JsonWriter writer, JsonAdapter<T> valueAdapter, String name, T value) throws IOException {
        writeNullable(writer, valueAdapter, name, value, false);
    }

    public static <T> void writeNullable(JsonWriter writer, JsonAdapter<T> valueAdapter, String name, T value, boolean enforced) throws IOException {
        writer.name(name);
        writeNullableValue(writer, valueAdapter, value, enforced);
    }

    public static <T> void writeNullableValue(JsonWriter writer, JsonAdapter<T> adapter, T value, boolean enforced) throws IOException {
        if (value != null) {
            adapter.toJson(writer, value);
        } else if (enforced) {
            boolean serializeFlag = writer.getSerializeNulls();
            try {
                writer.setSerializeNulls(true);
                writer.nullValue();
            } finally {
                writer.setSerializeNulls(serializeFlag);
            }
        } else {
            writer.nullValue();
        }
    }

}
