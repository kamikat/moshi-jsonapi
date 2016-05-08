package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

/**
 * JSON API Links Object
 */
@AutoValue
public abstract class Links implements TypeLinks {

    Links() { } // Seals class

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder self(Link value);
        public abstract Links build();
    }

    public static Builder builder() {
        return new AutoValue_Links.Builder();
    }

    public static Builder builder(Links value) {
        return new AutoValue_Links.Builder(value);
    }

    public static Links create(String self) {
        return create(Link.create(self));
    }

    public static Links create(Link self) {
        return builder().self(self).build();
    }

}
