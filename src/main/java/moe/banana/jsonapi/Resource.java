package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * JSON API Resource Object
 */
@AutoValue
public abstract class Resource extends OneOrManyValue<Resource> implements TypeResourceId {

    /**
     * the identifier of the resource, MAY be null when resource is not created yet.
     */
    @Nullable public abstract String id();

    /**
     * an attributes object representing some of the resource’s data.
     */
    @Optional
    @Nullable public abstract Object attributes();

    /**
     * accessor to `attributes`
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public final <T> T attrs() {
        return ((T) attributes());
    }

    /**
     * a relationships object describing relationships between the resource and other JSON API resources.
     */
    @Optional
    @Nullable public abstract Map<String, Relationship> relationships();

    /**
     * a links object containing links related to the resource.
     */
    @Optional
    @Nullable public abstract Links links();

    Resource() { } // Seals class

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder type(String value);
        public abstract Builder id(String value);
        public abstract Builder attributes(Object value);
        public abstract Builder relationships(Map<String, Relationship> value);
        public abstract Builder links(Links value);
        public abstract Builder meta(Object meta);
        public abstract Resource build();
    }

    public static Builder builder() {
        return new AutoValue_Resource.Builder();
    }

    public static Builder builder(Resource value) {
        return new AutoValue_Resource.Builder(value);
    }

    public static String typeOf(Class<?> type) {
        AttributesObject annotation = type.getAnnotation(AttributesObject.class);
        if (annotation == null) {
            throw new AssertionError("attributes class must declared with @AttributesObject annotation.");
        }
        return annotation.type();
    }

}
