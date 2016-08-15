package moe.banana.jsonapi2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * JSON API document object
 */
public final class Document implements Serializable {

    public final boolean hasMultipleResources;
    public final List<Resource> data = new ArrayList<>();
    public final List<Resource> included = new ArrayList<>();

    private LinkedHashMap<String, Resource> index;

    public Document(boolean hasMultipleResources) {
        this.hasMultipleResources = hasMultipleResources;
    }

    public void putData(Resource resource) {
        data.add(resource);
        resource._doc = this;
        addIndex(resource);
    }

    public void putIncluded(Resource resource) {
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

    private String indexName(String type, String id) {
        return type + ":" + id;
    }

    private void addIndex(Resource resource) {
        if (index == null) {
            index = new LinkedHashMap<>();
        }
        index.put(indexName(resource._type, resource._id), resource);
    }
}