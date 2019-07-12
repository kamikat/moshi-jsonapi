package moe.banana.jsonapi2;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public abstract class Document implements Serializable {

    List<Error> errors = new ArrayList<>(0);
    Map<ResourceIdentifier, Resource> included = new HashMap<>(0);

    @Nullable private JsonBuffer meta;
    @Nullable private JsonBuffer links;
    @Nullable private JsonBuffer jsonApi;

    public Document() {
    }

    public Document(Document document) {
        this.meta = document.meta;
        this.links = document.links;
        this.jsonApi = document.jsonApi;
        this.included.putAll(document.included);
        this.errors.addAll(document.errors);
    }

    @Deprecated
    public boolean include(Resource resource) {
        return addInclude(resource);
    }

    @Deprecated
    public boolean exclude(Resource resource) {
        return getIncluded().remove(resource);
    }

    public boolean addInclude(Resource resource) {
        return getIncluded().add(resource);
    }

    public Collection<Resource> getIncluded() {
        return new Collection<Resource>() {
            @Override
            public int size() {
                return included.size();
            }

            @Override
            public boolean isEmpty() {
                return included.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return included.containsValue(o);
            }

            @Override
            public Iterator<Resource> iterator() {
                return included.values().iterator();
            }

            @Override
            public Object[] toArray() {
                return included.values().toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return included.values().toArray(a);
            }

            @Override
            public boolean add(Resource resource) {
                bindDocument(Document.this, resource);
                included.put(new ResourceIdentifier(resource), resource);
                return true;
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof ResourceIdentifier) {
                    Resource resource = included.remove(new ResourceIdentifier(((ResourceIdentifier) o)));
                    bindDocument(null, resource);
                    return resource != null;
                }
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return included.values().containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends Resource> c) {
                for (Resource resource : c) {
                    add(resource);
                }
                return true;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                for (Object o : c) {
                    remove(o);
                }
                return true;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {
                bindDocument(null, included.values());
                included.clear();
            }
        };
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
    @Nullable
    public <T extends Resource> T find(ResourceIdentifier resourceIdentifier) {
        return (T) included.get(resourceIdentifier);
    }

    @Nullable
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

    @Nullable
    public JsonBuffer getMeta() {
        return meta;
    }

    public void setMeta(JsonBuffer meta) {
        this.meta = meta;
    }

    @Nullable
    public JsonBuffer getLinks() {
        return links;
    }

    public void setLinks(JsonBuffer links) {
        this.links = links;
    }

    @Nullable
    public JsonBuffer getJsonApi() {
        return jsonApi;
    }

    public void setJsonApi(JsonBuffer jsonApi) {
        this.jsonApi = jsonApi;
    }

    @SuppressWarnings("unchecked")
    public <DATA extends ResourceIdentifier> ArrayDocument<DATA> asArrayDocument() {
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

    public <DATA extends ResourceIdentifier> ObjectDocument<DATA> asObjectDocument() {
        return asObjectDocument(0);
    }

    @SuppressWarnings("unchecked")
    public <DATA extends ResourceIdentifier> ObjectDocument<DATA> asObjectDocument(int position) {
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

        Document document = (Document) o;

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

    static void bindDocument(Document document, Object resource) {
        if (resource instanceof ResourceIdentifier) {
            ((ResourceIdentifier) resource).setDocument(document);
        }
    }

    static void bindDocument(Document document, Collection<?> resources) {
        for (Object i : resources) {
            bindDocument(document, i);
        }
    }
}
