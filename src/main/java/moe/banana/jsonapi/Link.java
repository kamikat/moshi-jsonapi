package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

/**
 * JSON API Link Object
 */
@AutoValue
public abstract class Link implements TypeMeta {

    /**
     * a string containing the link’s URL.
     */
    public abstract String href();

    Link() { } // Seals class

    public Builder newBuilder() {
        return new AutoValue_Link.Builder(this);
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder href(String value);
        public abstract Builder meta(Object value);
        public abstract Link build();
    }

    public static Builder builder() {
        return new AutoValue_Link.Builder();
    }

    public static Link create(String href) {
        return create(href, null);
    }

    public static Link create(String href, Object meta) {
        return builder().href(href).meta(meta).build();
    }

}
