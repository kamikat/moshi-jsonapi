package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.HasOne;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "photos")
public class Photo extends Resource {
    public String url;
    public Boolean visible;
    public Double shutter;
    public Double longitude;
    public Double latitude;
    public HasOne<Person> author;
}
