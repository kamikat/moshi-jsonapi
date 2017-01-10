package moe.banana.jsonapi2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Document<DATA extends ResourceIdentifier> implements Serializable, Iterable<DATA> {

    List<DATA> data = new ArrayList<>(1);
    List<Resource> included = new ArrayList<>(0);
    List<Error> errors = new ArrayList<>(0);

    private JsonBuffer meta;
    private JsonBuffer links;
    private JsonBuffer jsonApi;

    boolean arrayFlag = false;

    public boolean set(DATA data) {
        arrayFlag = false;
        this.data.clear();
        data.setContext(this);
        return this.data.add(data);
    }

    public DATA get() {
        if (data.size() > 0) {
            return data.get(0);
        } else {
            return null;
        }
    }

    public boolean isList() {
        return arrayFlag;
    }

    public Document<DATA> asList() {
        arrayFlag = true;
        return this;
    }

    public boolean add(DATA data) {
        arrayFlag = true;
        data.setContext(this);
        return this.data.add(data);
    }

    public DATA get(int position) {
        return data.get(position);
    }

    public DATA remove(int position) {
        arrayFlag = true;
        return data.remove(position);
    }

    public boolean remove(DATA object) {
        arrayFlag = true;
        return data.remove(object);
    }

    public int size() {
        return data.size();
    }

    @Override
    public Iterator<DATA> iterator() {
        return data.iterator();
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
        if (~position == 0) {
            position = data.indexOf(resourceIdentifier);
            return ~position == 0 ? null : (T) data.get(position);
        }
        return (T) included.get(position);
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

    @Override
    public String toString() {
        return "Document{" +
                "data=" + data +
                ", included=" + included +
                ", errors=" + errors +
                ", meta=" + meta +
                ", links=" + links +
                ", jsonApi=" + jsonApi +
                ", arrayFlag=" + arrayFlag +
                '}';
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document<?> document = (Document<?>) o;

        if (arrayFlag != document.arrayFlag) return false;
        if (!data.equals(document.data)) return false;
        if (!included.equals(document.included)) return false;
        if (!errors.equals(document.errors)) return false;
        if (meta != null ? !meta.equals(document.meta) : document.meta != null) return false;
        if (links != null ? !links.equals(document.links) : document.links != null) return false;
        return jsonApi != null ? jsonApi.equals(document.jsonApi) : document.jsonApi == null;

    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + included.hashCode();
        result = 31 * result + errors.hashCode();
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (jsonApi != null ? jsonApi.hashCode() : 0);
        result = 31 * result + (arrayFlag ? 1 : 0);
        return result;
    }
}
