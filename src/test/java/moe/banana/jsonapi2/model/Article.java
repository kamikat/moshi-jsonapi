package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.HasMany;
import moe.banana.jsonapi2.HasOne;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "articles")
public class Article extends Resource {
    public String title;
    public HasOne<Person> author;
    public HasMany<Resource> comments;
}
