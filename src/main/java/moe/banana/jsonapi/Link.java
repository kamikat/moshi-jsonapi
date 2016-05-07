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

}
