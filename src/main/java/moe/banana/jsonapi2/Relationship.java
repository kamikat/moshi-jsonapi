package moe.banana.jsonapi2;

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

    public abstract RESULT get(Document<?> document);

}
