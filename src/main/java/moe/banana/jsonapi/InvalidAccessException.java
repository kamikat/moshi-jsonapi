package moe.banana.jsonapi;

public class InvalidAccessException extends AssertionError {

    public InvalidAccessException() {
    }

    public InvalidAccessException(Object detailMessage) {
        super(detailMessage);
    }

}
