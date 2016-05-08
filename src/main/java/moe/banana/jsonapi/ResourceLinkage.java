package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

/**
 * JSON API Resource Identifier Object
 */
@AutoValue
public abstract class ResourceLinkage extends OneOrManyValue<ResourceLinkage> implements TypeResourceId {

    ResourceLinkage() { } // Seals class

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder type(String value);
        public abstract Builder id(String value);
        public abstract Builder meta(Object meta);
        public abstract ResourceLinkage build();
    }

    public static Builder builder() {
        return new AutoValue_ResourceLinkage.Builder();
    }

    public static Builder builder(ResourceLinkage value) {
        return new AutoValue_ResourceLinkage.Builder(value);
    }

    public static ResourceLinkage create(String type, String id) {
        return builder().type(type).id(id).build();
    }

}
