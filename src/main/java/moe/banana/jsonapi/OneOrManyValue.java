package moe.banana.jsonapi;

import java.util.ArrayList;

public abstract class OneOrManyValue<T> extends ArrayList<T> {

    public final boolean one() {
        return size() == 0;
    }

    public final T only() {
        return get(0);
    }

    public abstract String toString();

    public final String toStringAsList() {
        return super.toString();
    }

}
