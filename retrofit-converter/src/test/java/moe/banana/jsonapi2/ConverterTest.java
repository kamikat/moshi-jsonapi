package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;
import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import retrofit2.Retrofit;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SuppressWarnings("all")
public class ConverterTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private TestApi api() throws Exception {
        Moshi moshi = TestUtil.moshi(Article.class, Comment.class);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:" + this.mockServerRule.getPort())
                .addConverterFactory(JsonApiConverterFactory.create(moshi))
                .build();
        return retrofit.create(TestApi.class);
    }

    private MockServerClient server() {
        return mockServerRule.getClient();
    }

    @Test
    public void deserialize_resources_to_list() throws Exception {
        server().when(request("/articles/1/comments").withMethod("GET"))
                .respond(response(TestUtil.fromResource("/multiple_comments.json")));
        List<Comment> comments = api().getComments("1").execute().body();
        assertThat(comments, notNullValue());
        assertThat(comments.get(0), instanceOf(Comment.class));
        assertThat(comments.get(1).getId(), equalTo("12"));
    }

    @Test
    public void deserialize_resources_to_array() throws Exception {
        server().when(request("/articles").withMethod("GET"))
                .respond(response(TestUtil.fromResource("/multiple_compound.json")));
        Article[] articles = api().listArticles().execute().body();
        assertThat(articles, notNullValue());
        assertThat(articles, instanceOf(Article[].class));
        assertThat(articles[0], notNullValue());
        assertThat(articles[0].getDocument(), notNullValue());
    }

    @Test
    public void deserialize_resource_object() throws Exception {
        server().when(request("/articles/1").withMethod("GET"))
                .respond(response(TestUtil.fromResource("/single.json")));
        Article article = api().getArticle("1").execute().body();
        assertThat(article, notNullValue());
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getDocument(), notNullValue());
    }

    @Test
    public void serialize_resource_object() throws Exception {
        server().when(
                request("/articles/1/comments")
                        .withMethod("POST")
                        .withHeader("Content-Type", "application/vnd.api+json")
                        .withBody("{\"data\":{\"type\":\"comments\",\"attributes\":{\"body\":\"Awesome!\"}}}"))
                .respond(response("{}"));
        Comment comment = new Comment();
        comment.setBody("Awesome!");
        Document response = api().addComment("1", comment).execute().body();
        assertThat(response, notNullValue());
        assertThat(response.asObjectDocument().hasData(), equalTo(false));
    }

    @Test
    public void serialize_resource_list() throws Exception {
        server().when(
                request("/articles/1/comments")
                        .withMethod("PUT")
                        .withHeader("Content-Type", "application/vnd.api+json")
                        .withBody("{\"data\":[{\"type\":\"comments\",\"attributes\":{\"body\":\"Awesome!\"}}]}"))
                .respond(response("{}"));
        Comment comment = new Comment();
        comment.setBody("Awesome!");
        Document response = api().addComments("1", Collections.singletonList(comment)).execute().body();
        assertThat(response, notNullValue());
        assertThat(response.asObjectDocument().hasData(), equalTo(false));
    }

    @Test
    public void serialize_relationship_array() throws Exception {
        server().when(
                request("/articles/1/relationships/tags")
                        .withMethod("PUT")
                        .withHeader("Content-Type", "application/vnd.api+json")
                        .withBody("{\"data\":[{\"type\":\"tags\",\"id\":\"1\"},{\"type\":\"tags\",\"id\":\"2\"}]}"))
                .respond(response("{}"));
        Document response = api().updateTags("1", new ResourceIdentifier[] {
                new ResourceIdentifier("tags", "1"),
                new ResourceIdentifier("tags", "2")
        }).execute().body();
        assertThat(response, notNullValue());
        assertThat(response.asObjectDocument().hasData(), equalTo(false));
    }

    @Test
    public void deserialize_relationship_object() throws Exception {
        server().when(request("/articles/1/relationships/tags").withMethod("GET"))
                .respond(response(TestUtil.fromResource("/relationship_multi.json")));
        ResourceIdentifier[] relData = api().getRelTags("1").execute().body();
        assertThat(relData.length, equalTo(2));
        assertThat(relData[0], instanceOf(ResourceIdentifier.class));
        assertThat(relData[1].getType(), equalTo("tags"));
    }

    @Test
    public void serialize_relationship_object() throws Exception {
        server().when(
                request("/articles/1/relationships/author")
                        .withMethod("PUT")
                        .withHeader("Content-Type", "application/vnd.api+json")
                        .withBody("{\"data\":{\"type\":\"people\",\"id\":\"1\"}}"))
                .respond(response("{}"));
        Document response = api().updateAuthor("1", new ResourceIdentifier("people", "1")).execute().body();
        assertThat(response, notNullValue());
        assertThat(response.asObjectDocument().hasData(), equalTo(false));
    }

}
