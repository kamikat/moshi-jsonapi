package moe.banana.jsonapi;

import android.support.annotation.Nullable;

public interface TypeLinks {

    /**
     * self link that identifies the resource/relation represented by the object.
     */
    @Optional
    @Nullable Link self();

}
