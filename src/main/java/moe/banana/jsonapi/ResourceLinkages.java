package moe.banana.jsonapi;

import javax.annotation.Nullable;

public final class ResourceLinkages extends ResourceLinkage {

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
}
