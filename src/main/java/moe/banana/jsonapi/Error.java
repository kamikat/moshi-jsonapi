package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

/**
 * JSON API Error Object
 */
@AutoValue
public abstract class Error implements TypeMeta {

    /**
     * a unique identifier for this particular occurrence of the problem.
     */
    @Optional
    @Nullable public abstract String id();

    /**
     * a links object containing links related to the error.
     */
    @Optional
    @Nullable public abstract Links links();

    /**
     * the HTTP status code applicable to this problem.
     */
    @Optional
    @Nullable public abstract String status();

    /**
     * an application-specific error code.
     */
    @Optional
    @Nullable public abstract String code();

    /**
     * a short, human-readable summary of the problem that
     * SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localization.
     */
    @Optional
    @Nullable public abstract String title();

    /**
     * a human-readable explanation specific to this occurrence of the problem.
     * Like title, this field’s value can be localized.
     */
    @Optional
    @Nullable public abstract String detail();

    /**
     * an object containing references to the source of the error
     */
    @Optional
    @Nullable public abstract Source source();

    @AutoValue
    public static abstract class Links implements TypeLinks {

        /**
         * a link that leads to further details about this particular occurrence of the problem.
         */
        @Optional
        @Nullable public abstract @Implicit Link about();

        Links() { } // Seals class

    }

    @AutoValue
    public static abstract class Source {

        /**
         * a JSON Pointer [RFC6901] to the associated entity in the request document
         */
        @Optional
        @Nullable public abstract String pointer();

        /**
         * a string indicating which URI query parameter caused the error.
         */
        @Optional
        @Nullable public abstract String parameter();

        Source() { } // Seals class

    }

    Error() { } // Seals class

}
