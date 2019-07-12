package moe.banana.jsonapi2;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public abstract class Resource extends ResourceIdentifier implements Serializable {

    public Resource() {
        setType(AnnotationUtils.typeNameOf(getClass()));
    }

    @Nullable private JsonBuffer links;

    @Nullable
    public JsonBuffer getLinks() {
        return links;
    }

    public void setLinks(JsonBuffer links) {
        this.links = links;
    }
}
