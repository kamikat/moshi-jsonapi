package moe.banana.jsonapi2;

import java.io.Serializable;

public final class ResourceLinkage implements Serializable {

    public final String type;
    public final String id;

    private ResourceLinkage(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static ResourceLinkage of(String type, String id) {
        return new ResourceLinkage(type, id);
    }

    public static ResourceLinkage of(Resource resource) {
        return of(resource._type, resource._id);
    }
}
