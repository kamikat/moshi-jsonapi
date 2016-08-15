package moe.banana.jsonapi2;

import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "comments")
class Comment extends Resource {
    public String body;
}
