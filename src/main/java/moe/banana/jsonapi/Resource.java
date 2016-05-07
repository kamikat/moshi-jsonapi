package moe.banana.jsonapi;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * JSON API Resource Object
 */
@AutoValue
public abstract class Resource extends OneOrManyValue<Resource> implements TypeResourceId {

    /**
     * an attributes object representing some of the resource’s data.
     */
    @Optional
    @Nullable public abstract Object attributes();

    /**
     * accessor to `attributes`
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public final <T> T attrs() {
        return ((T) attributes());
    }

    /**
     * a relationships object describing relationships between the resource and other JSON API resources.
     */
    @Optional
    @Nullable public abstract Map<String, Relationship> relationships();

    /**
     * a links object containing links related to the resource.
     */
    @Optional
    @Nullable public abstract Links links();

    Resource() { } // Seals class

}
