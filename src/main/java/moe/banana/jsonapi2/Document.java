package moe.banana.jsonapi2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Document<DATA extends ResourceIdentifier> implements Serializable {

    List<Resource> included = new ArrayList<>(0);
    List<Error> errors = new ArrayList<>(0);

    private JsonBuffer meta;
    private JsonBuffer links;
    private JsonBuffer jsonApi;

    public Document() {
    }

    public Document(Document<DATA> document) {
        this.meta = document.meta;
        this.links = document.links;
        this.jsonApi = document.jsonApi;
        this.included = document.included;
        this.errors = document.errors;
    }

    public boolean include(Resource resource) {
        resource.setContext(this);
        return included.add(resource);
    }

    public boolean exclude(Resource resource) {
        return included.remove(resource);
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
    public <T extends Resource> T find(ResourceIdentifier resourceIdentifier) {
        int position = included.indexOf(resourceIdentifier);
        return position >= 0 ? (T) included.get(position) : null;
    }

    public <T extends Resource> T find(String type, String id) {
        return find(new ResourceIdentifier(type, id));
    }

    public boolean errors(List<Error> errors) {
        this.errors.clear();
        if (errors != null) {
            this.errors.addAll(errors);
        }
        return true;
    }

    public List<Error> errors() {
        return this.errors;
    }

    public boolean hasError() {
        return errors.size() != 0;
    }

    public JsonBuffer getMeta() {
        return meta;
    }

    public void setMeta(JsonBuffer meta) {
        this.meta = meta;
    }

    public JsonBuffer getLinks() {
        return links;
    }

    public void setLinks(JsonBuffer links) {
        this.links = links;
    }

    public JsonBuffer getJsonApi() {
        return jsonApi;
    }

    public void setJsonApi(JsonBuffer jsonApi) {
        this.jsonApi = jsonApi;
    }

    public ArrayDocument<DATA> asArrayDocument() {
        if (this instanceof ArrayDocument) {
            return ((ArrayDocument<DATA>) this);
        } else if (this instanceof ObjectDocument) {
            ArrayDocument<DATA> document = new ArrayDocument<>(this);
            DATA data = ((ObjectDocument<DATA>) this).get();
            if (data != null) {
                document.add(data);
            }
            return document;
        }
        throw new AssertionError("unexpected document type");
    }

    public ObjectDocument<DATA> asObjectDocument() {
        return asObjectDocument(0);
    }

    public ObjectDocument<DATA> asObjectDocument(int position) {
        if (this instanceof ObjectDocument) {
            return ((ObjectDocument<DATA>) this);
        } else if (this instanceof ArrayDocument) {
            ObjectDocument<DATA> document = new ObjectDocument<>(this);
            if (((ArrayDocument<DATA>) this).size() > position) {
                document.set(((ArrayDocument<DATA>) this).get(position));
            }
            return document;
        }
        throw new AssertionError("unexpected document type");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document<?> document = (Document<?>) o;

        if (!included.equals(document.included)) return false;
        if (!errors.equals(document.errors)) return false;
        if (meta != null ? !meta.equals(document.meta) : document.meta != null) return false;
        if (links != null ? !links.equals(document.links) : document.links != null) return false;
        return jsonApi != null ? jsonApi.equals(document.jsonApi) : document.jsonApi == null;
    }

    @Override
    public int hashCode() {
        int result = included.hashCode();
        result = 31 * result + errors.hashCode();
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (jsonApi != null ? jsonApi.hashCode() : 0);
        return result;
    }
}
