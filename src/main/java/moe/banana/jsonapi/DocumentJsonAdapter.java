package moe.banana.jsonapi;

import com.squareup.moshi.*;

import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

class DocumentJsonAdapter extends JsonAdapter<Document> {

    private static final Set<Annotation> EMPTY = new HashSet<>();

    private JsonAdapter<Document> mAdapter;

    public DocumentJsonAdapter(Moshi moshi) {
        mAdapter = AutoValue_Document.typeAdapterFactory().create(Document.class, EMPTY, moshi);
    }

    @Override
    public Document fromJson(JsonReader reader) throws IOException {
        try {
            reader.peek();
        } catch (EOFException eof) {
            return null;
        }
        return mAdapter.fromJson(reader);
    }

    @Override
    public void toJson(JsonWriter writer, Document value) throws IOException {
        if (value != null) {
            mAdapter.toJson(writer, value);
        }
    }

}
