package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "comments")
public class Comment extends Resource {
    public String body;
}
