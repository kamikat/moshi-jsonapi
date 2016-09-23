package moe.banana.jsonapi2;

import java.io.Serializable;

/**
 * Resource linkage object defined in JSON API specification.
 */
@SuppressWarnings("deprecation")
public final class ResourceLinkage implements ResourceRef, Serializable {

    /**
     * Public access to this field is deprecated, use {@link #getType()} instead.
     */
    @Deprecated
    public final String type;

    /**
     * Public access to this field is deprecated, use {@link #getId()} ()} instead.
     */
    @Deprecated
    public final String id;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    private ResourceLinkage(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static ResourceLinkage of(String type, String id) {
        return new ResourceLinkage(type, id);
    }

    public static ResourceLinkage of(ResourceRef resource) {
        return of(resource.getType(), resource.getId());
    }

}
