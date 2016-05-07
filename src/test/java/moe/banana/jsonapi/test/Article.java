package moe.banana.jsonapi.test;

import com.google.auto.value.AutoValue;
import moe.banana.jsonapi.AttributesObject;

@AutoValue
@AttributesObject(type="articles")
abstract class Article {
    public abstract String title();
}
