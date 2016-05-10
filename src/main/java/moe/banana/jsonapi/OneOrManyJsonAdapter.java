package moe.banana.jsonapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public final class OneOrManyJsonAdapter<T extends OneOrManyValue<T>> extends JsonAdapter<T> {

    private JsonAdapter<T> mJsonAdapter;
    private ContainerFactory<T> mContainerFactory;

    public OneOrManyJsonAdapter(
            Class<T> clazz,
            ContainerFactory<T> containerFactory,
            Moshi moshi) {
        mJsonAdapter = moshi.adapter(clazz);
        mContainerFactory = containerFactory;
    }

    @Override
    public @OneOrMany T fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case BEGIN_ARRAY:
                reader.beginArray();
                T container = mContainerFactory.createContainer();
                while (reader.hasNext()) {
                    container.add(mJsonAdapter.fromJson(reader));
                }
                reader.endArray();
                return container;
            case BEGIN_OBJECT:
                return mJsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new JsonDataException();
        }
    }

    @Override
    public void toJson(JsonWriter writer, @OneOrMany T value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value.one()) {
            mJsonAdapter.toJson(writer, value);
        } else {
            writer.beginArray();
            for (T resource : value) {
                mJsonAdapter.toJson(writer, resource);
            }
            writer.endArray();
        }
    }

    public interface ContainerFactory<T extends OneOrManyValue<T>> {
        /**
         * create a container object of type `T`.
         * @return container instance
         */
        T createContainer();
    }

}
