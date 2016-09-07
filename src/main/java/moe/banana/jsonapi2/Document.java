package moe.banana.jsonapi2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * JSON API document object
 */
public final class Document implements Serializable {

    public final List<Resource> data = new ArrayList<>();
    public final List<Resource> included = new ArrayList<>();

    private LinkedHashMap<String, Resource> index;

    Document(Resource... data) {
        for (Resource r : data) {
            addData(r);
        }
    }

    public void addData(Resource resource) {
        data.add(resource);
        resource._doc = this;
        addIndex(resource);
    }

    public void addInclude(Resource resource) {
        included.add(resource);
        resource._doc = this;
        addIndex(resource);
    }

    public Resource find(String type, String id) throws ResourceNotFoundException {
        if (index != null) {
            final String key = indexName(type, id);
            if (index.containsKey(key)) {
                return index.get(key);
            }
        }
        throw new ResourceNotFoundException(type, id);
    }

    public Resource find(ResourceLinkage linkage) throws ResourceNotFoundException {
        return find(linkage.type, linkage.id);
    }

    public boolean contains(String type, String id) {
        if (index != null) {
            final String key = indexName(type, id);
            return index.containsKey(key);
        }
        return false;
    }

    public boolean contains(ResourceLinkage linkage)  {
        return contains(linkage.type, linkage.id);
    }


    private String indexName(String type, String id) {
        return type + ":" + id;
    }

    private void addIndex(Resource resource) {
        if (index == null) {
            index = new LinkedHashMap<>();
        }
        index.put(indexName(resource._type, resource._id), resource);
    }

    public static Document of(Resource... data) {
        return new Document(data);
    }

    public static Document create() {
        return new Document();
    }
}
