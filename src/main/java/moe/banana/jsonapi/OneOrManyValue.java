package moe.banana.jsonapi;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class OneOrManyValue<T> extends ArrayList<T> {

    public final boolean one() {
        return size() == 0;
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

    public final String toStringAsList() {
        return super.toString();
    }

}
