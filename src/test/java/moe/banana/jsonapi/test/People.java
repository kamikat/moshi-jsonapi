package moe.banana.jsonapi.test;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import moe.banana.jsonapi.AttributesObject;

@AutoValue
@AttributesObject(type="people")
abstract class People {
    public abstract @Json(name="first-name") String firstName();
    public abstract @Json(name="last-name") String lastName();
    public abstract String twitter();
}
