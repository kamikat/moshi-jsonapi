package moe.banana.jsonapi2;

import java.io.IOException;
import java.util.List;


public class JsonApiErrorException extends IOException {

    private final List<Error> errors;

    public JsonApiErrorException(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

}
