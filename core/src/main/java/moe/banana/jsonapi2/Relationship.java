package moe.banana.jsonapi2;

import org.jetbrains.annotations.NotNull;

abstract class Relationship<RESULT> {

    private JsonBuffer meta;
    private JsonBuffer links;

    public JsonBuffer getMeta() {
        return meta;
    }

    public JsonBuffer getLinks() {
        return links;
    }

    public void setMeta(JsonBuffer meta) {
        this.meta = meta;
    }

    public void setLinks(JsonBuffer links) {
        this.links = links;
    }

    public abstract RESULT get(@NotNull Document document);

}
