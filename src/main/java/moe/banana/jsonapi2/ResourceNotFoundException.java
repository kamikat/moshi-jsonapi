package moe.banana.jsonapi2;

public class ResourceNotFoundException extends RuntimeException {

    ResourceNotFoundException(String type, String id) {
        super("Resource " + type + "[id=" + id + "] could not be found.");
    }

}
