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

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceLinkage that = (ResourceLinkage) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    public static ResourceLinkage of(String type, String id) {
        return new ResourceLinkage(type, id);
    }

    public static ResourceLinkage of(ResourceRef resource) {
        return of(resource.getType(), resource.getId());
    }

}
