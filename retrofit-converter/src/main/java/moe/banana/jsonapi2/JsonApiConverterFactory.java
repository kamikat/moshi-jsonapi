package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import moe.banana.jsonapi2.ArrayDocument;
import moe.banana.jsonapi2.Document;
import moe.banana.jsonapi2.ObjectDocument;
import moe.banana.jsonapi2.Resource;
import moe.banana.jsonapi2.ResourceIdentifier;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

@SuppressWarnings("unchecked")
public final class JsonApiConverterFactory extends Converter.Factory {

    public static JsonApiConverterFactory create() {
        return create(new Moshi.Builder().build());
    }

    public static JsonApiConverterFactory create(Moshi moshi) {
        return new JsonApiConverterFactory(moshi, false);
    }

    private final Moshi moshi;
    private final boolean lenient;

    private JsonApiConverterFactory(Moshi moshi, boolean lenient) {
        if (moshi == null) throw new NullPointerException("moshi == null");
        this.moshi = moshi;
        this.lenient = lenient;
    }

    public JsonApiConverterFactory asLenient() {
        return new JsonApiConverterFactory(moshi, true);
    }

    private JsonAdapter<?> getAdapterFromType(Type type) {
        Class<?> rawType = Types.getRawType(type);
        JsonAdapter<?> adapter;
        if (rawType.isArray() && ResourceIdentifier.class.isAssignableFrom(rawType.getComponentType())) {
            adapter = moshi.adapter(Types.newParameterizedType(Document.class, rawType.getComponentType()));
        } else if (List.class.isAssignableFrom(rawType) && type instanceof ParameterizedType) {
            Type typeParameter = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (typeParameter instanceof Class<?> && ResourceIdentifier.class.isAssignableFrom((Class<?>) typeParameter)) {
                adapter = moshi.adapter(Types.newParameterizedType(Document.class, typeParameter));
            } else {
                return null;
            }
        } else if (ResourceIdentifier.class.isAssignableFrom(rawType)) {
            adapter = moshi.adapter(Types.newParameterizedType(Document.class, rawType));
        } else if (Document.class.isAssignableFrom(rawType)) {
            adapter = moshi.adapter(Types.newParameterizedType(Document.class, Resource.class));
        } else {
            return null;
        }
        return adapter;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        JsonAdapter<?> adapter = getAdapterFromType(type);
        if (adapter == null) {
            return null;
        }
        if (lenient) {
            adapter = adapter.lenient();
        }
        return new MoshiResponseBodyConverter<>((JsonAdapter<Document>) adapter, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        JsonAdapter<?> adapter = getAdapterFromType(type);
        if (adapter == null) {
            return null;
        }
        if (lenient) {
            adapter = adapter.lenient();
        }
        return new MoshiRequestBodyConverter<>((JsonAdapter<Document>) adapter, type);
    }

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/vnd.api+json");

    private static class MoshiResponseBodyConverter<R> implements Converter<ResponseBody, R> {
        private final JsonAdapter<Document> adapter;
        private final Class<R> rawType;

        MoshiResponseBodyConverter(JsonAdapter<Document> adapter, Type type) {
            this.adapter = adapter;
            this.rawType = (Class<R>) Types.getRawType(type);
        }

        @Override
        public R convert(ResponseBody value) throws IOException {
            try {
                Document document = adapter.fromJson(value.source());
                if (Document.class.isAssignableFrom(rawType)) {
                    return (R) document;
                } else if (List.class.isAssignableFrom(rawType)) {
                    ArrayDocument arrayDocument = document.asArrayDocument();
                    List a;
                    if (rawType.isAssignableFrom(ArrayList.class)) {
                        a = new ArrayList();
                    } else {
                        a = (List) rawType.newInstance();
                    }
                    a.addAll(arrayDocument);
                    return (R) a;
                } else if (rawType.isArray()) {
                    ArrayDocument<?> arrayDocument = document.asArrayDocument();
                    Object a = Array.newInstance(rawType.getComponentType(), arrayDocument.size());
                    for (int i = 0; i != Array.getLength(a); i++) {
                        Array.set(a, i, arrayDocument.get(i));
                    }
                    return (R) a;
                } else {
                    return (R) document.asObjectDocument().get();
                }
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot find default constructor of [" + rawType.getCanonicalName() + "].", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access default constructor of [" + rawType.getCanonicalName() + "].", e);
            } finally {
                value.close();
            }
        }
    }

    private static class MoshiRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private final JsonAdapter<Document> adapter;
        private final Class<T> rawType;

        MoshiRequestBodyConverter(JsonAdapter<Document> adapter, Type type) {
            this.adapter = adapter;
            this.rawType = (Class<T>) Types.getRawType(type);
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Document document;
            if (Document.class.isAssignableFrom(rawType)) {
                document = (Document) value;
            } else if (List.class.isAssignableFrom(rawType)) {
                ArrayDocument arrayDocument = new ArrayDocument();
                List a = ((List) value);
                if (!a.isEmpty() && a.get(0) != null && ((ResourceIdentifier) a.get(0)).getContext() != null) {
                    arrayDocument = ((ResourceIdentifier) a.get(0)).getContext().asArrayDocument();
                }
                arrayDocument.addAll(a);
                document = arrayDocument;
            } else if (rawType.isArray()) {
                ArrayDocument arrayDocument = new ArrayDocument();
                if (Array.getLength(value) > 0 && ((ResourceIdentifier) Array.get(value, 0)).getContext() != null) {
                    arrayDocument = ((ResourceIdentifier) Array.get(value, 0)).getContext().asArrayDocument();
                }
                for (int i = 0; i != Array.getLength(value); i++) {
                    arrayDocument.add((ResourceIdentifier) Array.get(value, i));
                }
                document = arrayDocument;
            } else {
                ResourceIdentifier data = ((ResourceIdentifier) value);
                ObjectDocument objectDocument = new ObjectDocument();
                if (data.getDocument() != null) {
                    objectDocument = data.getDocument().asObjectDocument();
                }
                objectDocument.set(data);
                document = objectDocument;
            }
            Buffer buffer = new Buffer();
            adapter.toJson(buffer, document);
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

}