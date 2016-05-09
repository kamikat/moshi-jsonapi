package moe.banana.jsonapi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.annotation.*;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Qualifies a meta object that contains non-standard meta-information.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AttributesObject {
    String type();
    Class<? extends JsonAdapter.Factory> factory() default DefaultFactory.class;
    final class DefaultFactory implements JsonAdapter.Factory {
        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            return null;
        }
    }
}
