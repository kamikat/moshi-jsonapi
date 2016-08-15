package moe.banana.jsonapi2;

@JsonApi(type = "articles")
class Article extends Resource {
    public String title;
    public HasOne<Person> author;
    public HasMany<Resource> comments;
}
