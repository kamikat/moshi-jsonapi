package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

/**
 * JSON API Relationship Object
 */
@AutoValue
public abstract class Relationship implements TypeMeta {

    /**
     * a links object
     */
    @Nullable public abstract Links links();

    /**
     * resource linkage description.
     */
    @Nullable public abstract @OneOrMany ResourceLinkage data();

    @AutoValue
    public static abstract class Links implements TypeLinks {

        /**
         * a related resource link represents a resource relationship.
         */
        @Optional
        @Nullable public abstract @Implicit Link related();

        Links() { } // Seals class

        @AutoValue.Builder
        public static abstract class Builder {
            public abstract Builder self(Link value);
            public abstract Builder related(Link value);
            public abstract Links build();
        }

        public static Builder builder() {
            return new AutoValue_Relationship_Links.Builder();
        }

        public Builder builder(Links value) {
            return new AutoValue_Relationship_Links.Builder(value);
        }

    }

    Relationship() { } // Seals class

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder links(Links value);
        public abstract Builder data(ResourceLinkage value);
        public abstract Builder meta(Object meta);
        public abstract Relationship build();
    }

    public static Builder builder() {
        return new AutoValue_Relationship.Builder();
    }

    public static Builder builder(Relationship value) {
        return new AutoValue_Relationship.Builder(value);
    }

}
