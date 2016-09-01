package moe.banana.jsonapi2;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class AnnotationUtils {

    public static final Set<Annotation> NO_ANNOTATIONS = Collections.emptySet();

    public static Set<? extends Annotation> jsonAnnotations(Annotation[] annotations) {
        Set<Annotation> result = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(JsonQualifier.class)) {
                if (result == null) result = new LinkedHashSet<>();
                result.add(annotation);
            }
        }
        return result != null ? Collections.unmodifiableSet(result) : NO_ANNOTATIONS;
    }
}
