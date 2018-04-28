package moe.banana.jsonapi2;

import java.io.Serializable;

public abstract class Resource extends ResourceIdentifier implements Serializable {

    public Resource() {
        setType(AnnotationUtils.typeNameOf(getClass()));
    }

    private JsonBuffer links;

    public JsonBuffer getLinks() {
        return links;
    }

    public void setLinks(JsonBuffer links) {
        this.links = links;
    }
}
