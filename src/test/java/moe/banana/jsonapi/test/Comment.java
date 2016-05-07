package moe.banana.jsonapi.test;

import com.google.auto.value.AutoValue;
import moe.banana.jsonapi.AttributesObject;

@AutoValue
@AttributesObject(type="comments")
abstract class Comment {
    public abstract String body();
}
