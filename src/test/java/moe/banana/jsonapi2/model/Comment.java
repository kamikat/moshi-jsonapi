package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.HasOne;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "comments")
public class Comment extends Resource {
    private String body;
    private HasOne<Person> author;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HasOne<Person> getAuthor() {
        return author;
    }

    public void setAuthor(HasOne<Person> author) {
        this.author = author;
    }
}
