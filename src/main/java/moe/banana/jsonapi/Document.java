package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.List;

/**
 * JSON API Spec v1.0 Top-level document
 */
@AutoValue
public abstract class Document implements TypeMeta {

    /**
     * the document’s “primary data”
     *
     * (exclusive to `errors`)
     */
    @Nullable public abstract @OneOrMany Resource data();

    /**
     * an array of error objects
     *
     * (exclusive to `data`)
     */
    @Nullable
    public abstract List<Error> errors();

    /**
     * a meta object that contains non-standard meta-information.
     *
     * (must present if neither of `data` and `errors` exists)
     */
    @Nullable public abstract Object meta();

    /**
     * a links object related to the primary data.
     */
    @Optional
    @Nullable public abstract Links links();

    /**
     * an array of resource objects that are related to the primary data and/or each other (“included resources”).
     */
    @Optional
    @Nullable public abstract List<Resource> included();

    /**
     * an object describing the server’s implementation
     */
    @Optional
    @Nullable public abstract JsonApi jsonapi();

    /**
     * JSON API Top-level Links Object
     */
    @AutoValue
    public abstract static class Links implements TypeLinks {

        /**
         * pagination: the first page of data
         */
        @Optional
        @Nullable public abstract @Implicit Link first();

        /**
         * pagination: the last page of data
         */
        @Optional
        @Nullable public abstract @Implicit Link last();

        /**
         * pagination: the previous page of data
         */
        @Optional
        @Nullable public abstract @Implicit Link prev();

        /**
         * pagination: the next page of data
         */
        @Optional
        @Nullable public abstract @Implicit Link next();

        Links() { } // Seals class

    }

    Document() { } // Seals class

}
