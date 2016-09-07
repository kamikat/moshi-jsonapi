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

    public T get(T defaultValue) {
        try {
            return get();
        }
        catch (ResourceNotFoundException e){
            return defaultValue;
        }
    }

    public boolean resourceExists(){
        return resource._doc.contains(linkage);
    }

    public static <T extends Resource> HasOne<T> create(Resource resource, T linked) {
        return create(resource, ResourceLinkage.of(linked));
    }

    public static <T extends Resource> HasOne<T> create(Resource resource, ResourceLinkage linkage) {
        return new HasOne<>(resource, linkage);
    }
}
