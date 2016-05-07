package moe.banana.jsonapi.test;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

class TestAdapterFactory implements JsonAdapter.Factory {

    JsonAdapter.Factory[] mFactoryArray = new JsonAdapter.Factory[] {
            AutoValue_Article.typeAdapterFactory(),
            AutoValue_Comment.typeAdapterFactory(),
            AutoValue_People.typeAdapterFactory(),
    };

    @Override
    public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
        for (JsonAdapter.Factory factory: mFactoryArray) {
            JsonAdapter<?> adapter = factory.create(type, annotations, moshi);
            if (adapter != null) {
                return adapter;
            }
        }
        return null;
    }

}
