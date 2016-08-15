package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import moe.banana.jsonapi2.model.Article;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ALL")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ResourceUnitTest {

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
        builder.add(ResourceAdapterFactory.builder().build());
        return builder.build();
    }

    @Test
    public void deserialization() throws Exception {
        JsonAdapter<Article> articleAdapter = new Resource.Adapter<Article>(Article.class, moshi());
        Article a = articleAdapter.fromJson(JSON);
        assertThat(a._id, equalTo("1"));
        assertThat(a._type, equalTo("articles"));
        assertThat(a.title, equalTo("JSON API paints my bikeshed!"));
        assertThat(a.author.linkage.id, equalTo("9"));
        assertThat(a.author.linkage.type, equalTo("people"));
        assertThat(a.comments.linkages.length, equalTo(2));
        assertThat(a.comments.linkages[0].type, equalTo("comments"));
        assertThat(a.comments.linkages[0].id, equalTo("5"));
        assertThat(a.comments.linkages[1].type, equalTo("comments"));
        assertThat(a.comments.linkages[1].id, equalTo("12"));
    }

    @Test
    public void deserialization_x100() throws Exception {
        JsonAdapter<Article> articleAdapter = new Resource.Adapter<Article>(Article.class, moshi());
        for (int i = 0; i != 100; i++) {
            articleAdapter.fromJson(JSON);
        }
    }

    @Test
    public void serialization_empty() throws Exception {
        JsonAdapter<Article> articleAdapter = new Resource.Adapter<Article>(Article.class, moshi());
        assertThat(articleAdapter.toJson(new Article()), equalTo("{\"type\":\"articles\"}"));
    }

    @Test
    public void serialization_attributes() throws Exception {
        JsonAdapter<Article> articleAdapter = new Resource.Adapter<Article>(Article.class, moshi());
        Article a = new Article();
        a.title = "It sucks!";
        assertThat(articleAdapter.toJson(a), equalTo("{\"type\":\"articles\",\"attributes\":{\"title\":\"It sucks!\"}}"));
    }

    @Test
    public void serialization_relationships() throws Exception {
        JsonAdapter<Article> articleAdapter = new Resource.Adapter<Article>(Article.class, moshi());
        Article a = new Article();
        a.author = new HasOne<>(a, ResourceLinkage.of("people", "2"));
        assertThat(articleAdapter.toJson(a), equalTo("{\"type\":\"articles\",\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"2\"}}}}"));
    }
}
