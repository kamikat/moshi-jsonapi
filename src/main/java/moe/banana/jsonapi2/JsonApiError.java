package moe.banana.jsonapi2;

import java.util.List;

public class JsonApiError {

    private List<Error> errors;

    public JsonApiError() {
    }

    public JsonApiError(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

}
