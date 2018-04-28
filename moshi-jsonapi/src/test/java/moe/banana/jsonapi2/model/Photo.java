package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.HasOne;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Policy;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "photos")
public class Photo extends Resource {

    private String url;
    private Boolean visible;
    private Double shutter;
    private Location location;
    private HasOne<Person> author;

    private @Color
    int color;

    public static class Location {
        public Double longitude;
        public Double latitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Double getShutter() {
        return shutter;
    }

    public void setShutter(Double shutter) {
        this.shutter = shutter;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HasOne<Person> getAuthor() {
        return author;
    }

    public void setAuthor(HasOne<Person> author) {
        this.author = author;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @JsonApi(type = "photos", priority = -1)
    public static class Photo2 extends Photo {

    }

    @JsonApi(type = "photos")
    public static class Photo3 extends Photo {

    }

    @JsonApi(type = "photos", policy = Policy.SERIALIZATION_ONLY)
    public static class Photo4 extends Photo {

    }

    @JsonApi(type = "photos", policy = Policy.DESERIALIZATION_ONLY)
    public static class Photo5 extends Photo {

    }
}
