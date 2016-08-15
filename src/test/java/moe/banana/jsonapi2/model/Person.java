package moe.banana.jsonapi2.model;

import com.squareup.moshi.Json;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "people")
public class Person extends Resource {
    public @Json(name="first-name") String firstName;
    public @Json(name="last-name") String lastName;
    public String twitter;
    public Integer age;
}
