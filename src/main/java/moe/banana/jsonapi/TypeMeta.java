package moe.banana.jsonapi;

import android.support.annotation.Nullable;

public interface TypeMeta {

    /**
     * a meta object that contains non-standard meta-information.
     */
    @Optional
    @Nullable Object meta();

}
