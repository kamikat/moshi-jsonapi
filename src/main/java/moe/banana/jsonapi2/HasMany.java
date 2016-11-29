package moe.banana.jsonapi2;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

@SuppressWarnings("deprecation")
public final class HasMany<T extends Resource> implements Relationship, Iterable<T>, Serializable {

    /**
     * Public access to this field is deprecated, use {@link #getLinkages()} instead.
     */
    @Deprecated
    public final ResourceLinkage[] linkages;

    private final Class<T> type;
    private final Resource resource;

    HasMany(Class<T> type, Resource resource, ResourceLinkage[] linkages) {
        this.type = type;
        this.resource = resource;
        this.linkages = linkages;
    }

    @Deprecated
    public T[] get() {
        return getAll();
    }

    @SuppressWarnings("unchecked")
    public T[] getAll() {
        T[] array = (T[]) Array.newInstance(type, linkages.length);
        for (int i = 0; i != linkages.length; i++) {
            array[i] = (T) resource.find(linkages[i]);
        }
        return array;
    }

    /**
     * Retrieve linkage information.
     *
     * @return resource linkage objects.
     */
    public ResourceLinkage[] getLinkages() {
        return linkages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HasMany<?> hasMany = (HasMany<?>) o;

        return Arrays.equals(linkages, hasMany.linkages);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(linkages);
    }

    /**
     * Iterates over linked resources.
     *
     * @return iterator whose {@link Iterator#next()} returns linked Resource
     *         or null if linkage cannot be resolved with document.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return linkages != null && i != linkages.length;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                return (T) resource.find(linkages[i++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Resource> HasMany<T> create(Resource resource, T... linked) {
        ResourceLinkage[] linkages = new ResourceLinkage[linked.length];
        for (int i = 0; i != linkages.length; i++) {
            linkages[i] = ResourceLinkage.of(linked[i]);
        }
        return create(resource, (Class<T>) linked.getClass().getComponentType(), linkages);
    }

    public static HasMany<? extends Resource> create(Resource resource, ResourceLinkage... linkage) {
        return create(resource, Resource.class, linkage);
    }

    public static <T extends Resource> HasMany<T> create(Resource resource, Class<T> componentType, ResourceLinkage... linkage) {
        return new HasMany<>(componentType, resource, linkage);
    }
}
