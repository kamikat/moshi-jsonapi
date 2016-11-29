package moe.banana.jsonapi2;

import java.io.Serializable;

@SuppressWarnings("deprecation")
public final class HasOne<T extends Resource> implements Relationship, Serializable {

    /**
     * Public access to this field is deprecated, use {@link #getLinkage()} instead.
     */
    @Deprecated
    public final ResourceLinkage linkage;

    private final Resource resource;

    HasOne(Resource resource, ResourceLinkage linkage) {
        this.resource = resource;
        this.linkage = linkage;
    }

    /**
     * Resolve relationship.
     *
     * @return referenced resource or null if not found.
     */
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) resource.find(linkage);
    }

    /**
     * Resolve relationship with a default value.
     *
     * @param defaultValue value to return if referenced resource cannot be found.
     * @return referenced object or default value if not found.
     */
    public T get(T defaultValue) {
        T obj = get();
        if (obj == null) {
            return defaultValue;
        } else {
            return obj;
        }
    }

    /**
     * Retrieve linkage information.
     *
     * @return resource linkage object.
     */
    public ResourceLinkage getLinkage() {
        return linkage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HasOne<?> hasOne = (HasOne<?>) o;

        return linkage != null ? linkage.equals(hasOne.linkage) : hasOne.linkage == null;
    }

    @Override
    public int hashCode() {
        return linkage != null ? linkage.hashCode() : 0;
    }

    public static <T extends Resource> HasOne<T> create(Resource resource, T linked) {
        return create(resource, ResourceLinkage.of(linked));
    }

    public static <T extends Resource> HasOne<T> create(Resource resource, ResourceLinkage linkage) {
        return new HasOne<>(resource, linkage);
    }
}
