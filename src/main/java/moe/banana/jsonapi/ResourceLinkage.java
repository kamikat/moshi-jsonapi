package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

/**
 * JSON API Resource Identifier Object
 */
@AutoValue
public abstract class ResourceLinkage extends OneOrManyValue<ResourceLinkage> implements TypeResourceId {

    ResourceLinkage() { } // Seals class

}
