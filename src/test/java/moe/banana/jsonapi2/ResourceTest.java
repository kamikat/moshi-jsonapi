package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import moe.banana.jsonapi2.model.Article;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceTest {

    private static final String JSON = "{" +
            "    \"type\": \"articles\"," +
            "    \"id\": \"1\"," +
            "    \"attributes\": {" +
            "      \"title\": \"JSON API paints my bikeshed!\"" +
            "    }," +
            "    \"relationships\": {" +
            "      \"author\": {" +
            "        \"links\": {" +
            "          \"self\": \"http://example.com/articles/1/relationships/author\"," +
            "          \"related\": \"http://example.com/articles/1/author\"" +
            "        }," +
            "        \"data\": { \"type\": \"people\", \"id\": \"9\" }" +
            "      }," +
            "      \"comments\": {" +
            "        \"links\": {" +
            "          \"self\": \"http://example.com/articles/1/relationships/comments\"," +
            "          \"related\": \"http://example.com/articles/1/comments\"" +
            "        }," +
            "        \"data\": [" +
            "          { \"type\": \"comments\", \"id\": \"5\" }," +
            "          { \"type\": \"comments\", \"id\": \"12\" }" +
            "        ]" +
            "      }" +
            "    }," +
            "    \"links\": {" +
            "      \"self\": \"http://example.com/articles/1\"" +
            "    }" +
            "}";

    public static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .build());
        return builder.build();
    }

    @Test
    public void deserialization() throws Exception {
        Article a = moshi().adapter(Article.class).fromJson(JSON);
        assertThat(a.getId(), equalTo("1"));
        assertThat(a.getType(), equalTo("articles"));
        assertThat(a.title, equalTo("JSON API paints my bikeshed!"));
        assertThat(a.author.get().getId(), equalTo("9"));
        assertThat(a.author.get().getType(), equalTo("people"));
        assertThat(a.comments.size(), equalTo(2));
        assertThat(a.comments.get(0).getType(), equalTo("comments"));
        assertThat(a.comments.get(0).getId(), equalTo("5"));
        assertThat(a.comments.get(1).getType(), equalTo("comments"));
        assertThat(a.comments.get(1).getId(), equalTo("12"));
    }

    @Test
    public void deserialization_x100() throws Exception {
        JsonAdapter<Article> articleAdapter = moshi().adapter(Article.class);
        for (int i = 0; i != 100; i++) {
            articleAdapter.fromJson(JSON);
        }
    }

    @Test
    public void serialization_empty() throws Exception {
        assertThat(moshi().adapter(Article.class).toJson(new Article()), equalTo("{\"type\":\"articles\"}"));
    }

    @Test
    public void serialization_attributes() throws Exception {
        Article a = new Article();
        a.title = "It sucks!";
        a.ignored = "should be ok to set";
        assertThat(moshi().adapter(Article.class).toJson(a), equalTo(
                "{\"type\":\"articles\",\"attributes\":{\"title\":\"It sucks!\"}}"));
    }

    @Test
    public void serialization_relationships() throws Exception {
        Article a = new Article();
        a.author = new HasOne<>(new ResourceIdentifier("people", "2"));
        assertThat(moshi().adapter(Article.class).toJson(a), equalTo(
                "{\"type\":\"articles\",\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"2\"}}}}"));
    }
}
