package moe.banana.jsonapi2;

public final class ResourceLinkage {

    public final String type;
    public final String id;

    private ResourceLinkage(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public static ResourceLinkage of(String type, String id) {
        return new ResourceLinkage(type, id);
    }
}
