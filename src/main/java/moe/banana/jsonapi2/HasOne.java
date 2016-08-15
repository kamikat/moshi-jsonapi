package moe.banana.jsonapi2;

import java.io.Serializable;

public final class HasOne<T extends Resource> implements Relationship<T>, Serializable {

    public final ResourceLinkage linkage;

    private final Resource resource;

    HasOne(Resource resource, ResourceLinkage linkage) {
        this.resource = resource;
        this.linkage = linkage;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() throws ResourceNotFoundException {
        return (T) resource._doc.find(linkage);
    }

    public static <T extends Resource> HasOne<T> linkage(Resource resource, T linked) {
        return new HasOne<>(resource, ResourceLinkage.of(linked));
    }
}
