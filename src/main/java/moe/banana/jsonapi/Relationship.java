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

    }

    Relationship() { } // Seals class

}
