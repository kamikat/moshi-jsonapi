package moe.banana.jsonapi;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class OneOrManyValue<T> extends ArrayList<T> {

    /**
     * @return true if the object represents the object itself, false vice versa
     */
    public abstract boolean one();

    /**
     * @return true if the object represents an array of the object, false vice versa
     */
    public final boolean many() {
        return !one();
    }

    public final T only() {
        return get(0);
    }

    @SafeVarargs
    public final OneOrManyValue<T> append(T... array) {
        addAll(Arrays.asList(array));
        return this;
    }

    public abstract String toString();

    public abstract int hashCode();

    public abstract boolean equals(Object o);

    public final String toStringAsList() {
        return super.toString();
    }

    public final int hashCodeAsList() {
        return super.hashCode();
    }

    public final boolean equalsAsList(Object o) {
        return super.equals(o);
    }

}
