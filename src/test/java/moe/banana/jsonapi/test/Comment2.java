package moe.banana.jsonapi.test;

import com.google.auto.value.AutoValue;
import moe.banana.jsonapi.AttributesObject;

@AutoValue
@AttributesObject(type="comments", factory = AutoValue_Comment2.AutoValue_Comment2JsonAdapterFactory.class)
abstract class Comment2 {
    public abstract String body();
}
