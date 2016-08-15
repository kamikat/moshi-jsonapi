package moe.banana.jsonapi2;

import java.io.Serializable;
import java.lang.reflect.Array;

public final class HasMany<T extends Resource> implements Relationship<T[]>, Serializable {

    public final ResourceLinkage[] linkages;

    private final Class<T> type;
    private final Resource resource;

    HasMany(Class<T> type, Resource resource, ResourceLinkage[] linkages) {
        this.type = type;
        this.resource = resource;
        this.linkages = linkages;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] get() throws ResourceNotFoundException {
        T[] array = (T[]) Array.newInstance(type, linkages.length);
        for (int i = 0; i != linkages.length; i++) {
            array[i] = (T) resource._doc.find(linkages[i]);
        }
        return array;
    }
}
