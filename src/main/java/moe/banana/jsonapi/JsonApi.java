package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

/**
 * JSON API `jsonapi` Object
 */
@AutoValue
public abstract class JsonApi implements TypeMeta {

    /**
     * the highest JSON API version supported.
     */
    @Nullable public abstract String version();

    JsonApi() { } // Seals class

}
