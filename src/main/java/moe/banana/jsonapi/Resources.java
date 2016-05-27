package moe.banana.jsonapi;

import javax.annotation.Nullable;
import java.util.Map;

public final class Resources extends Resource {

    @Nullable
    @Override
    public Object attributes() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Map<String, Relationship> relationships() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Links links() {
        throw new InvalidAccessException();
    }

    @Override
    public String id() {
        throw new InvalidAccessException();
    }

    @Override
    public String type() {
        throw new InvalidAccessException();
    }

    @Nullable
    @Override
    public Object meta() {
        throw new InvalidAccessException();
    }

    @Override
    public String toString() {
        return toStringAsList();
    }

    @Override
    public int hashCode() {
        return hashCodeAsList();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return equalsAsList(o);
    }

    @Override
    public boolean one() {
        return false;
    }

}
