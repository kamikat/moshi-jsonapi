package moe.banana.jsonapi2;

import java.io.Serializable;
import java.util.*;

public abstract class Document<DATA extends ResourceIdentifier> implements Serializable {

    List<Error> errors = new ArrayList<>(0);
    Map<ResourceIdentifier, Resource> included = new HashMap<>(0);

    private JsonBuffer meta;
    private JsonBuffer links;
    private JsonBuffer jsonApi;

    public Document() {
    }

    public Document(Document<DATA> document) {
        this.meta = document.meta;
        this.links = document.links;
        this.jsonApi = document.jsonApi;
        this.included.putAll(document.included);
        this.errors.addAll(document.errors);
    }

    public boolean include(Resource resource) {
        resource.setDocument(this);
        included.put(new ResourceIdentifier(resource), resource);
        return true;
    }

    public boolean exclude(Resource resource) {
        if (resource.getDocument() == this) {
            resource.setDocument(null);
        }
        included.remove(new ResourceIdentifier(resource));
        return true;
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
    public <T extends Resource> T find(ResourceIdentifier resourceIdentifier) {
        return (T) included.get(resourceIdentifier);
    }

    public <T extends Resource> T find(String type, String id) {
        return find(new ResourceIdentifier(type, id));
    }

    @Deprecated
    public boolean errors(List<Error> errors) {
        return setErrors(errors);
    }

    @Deprecated
    public List<Error> errors() {
        return getErrors();
    }

    public boolean addError(Error error) {
        return errors.add(error);
    }

    public boolean setErrors(Collection<Error> errors) {
        this.errors.clear();
        if (errors != null) {
            this.errors.addAll(errors);
        }
        return true;
    }

    public List<Error> getErrors() {
        return errors;
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
