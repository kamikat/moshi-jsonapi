package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Person;
import moe.banana.jsonapi2.model.PlainObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
public class ResourceTest {

    private static final String JSON = "{" +
            "    \"type\": \"articles\"," +
            "    \"id\": \"1\"," +
            "    \"attributes\": {" +
            "      \"title\": \"JSON API paints my bikeshed!\"," +
            "      \"ignored\": \"JSON API paints my bikeshed!\"," +
            "      \"nullable\": null" +
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

    @Test
    public void equality() throws Exception {
        Article a = TestUtil.moshi(Article.class).adapter(Article.class).fromJson(JSON);
        Article b = new Article();
        assertNotEquals(b, a);
        assertEquals(b, new Article());
        assertNotEquals(b, null);
        b.setId(a.getId());
        assertEquals(b, a);
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void deserialization() throws Exception {
        Article article = TestUtil.moshi(Article.class).adapter(Article.class).fromJson(JSON);
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getType(), equalTo("articles"));
        assertThat(article.getTitle(), equalTo("JSON API paints my bikeshed!"));
        assertThat(article.getIgnored(), nullValue());
        assertThat(article.getNullable(), nullValue());
        assertThat(article.getAuthor().get(), equalTo(new ResourceIdentifier("people", "9")));
        assertThat(article.getComments().get(), hasItems(
                new ResourceIdentifier("comments", "5"),
                new ResourceIdentifier("comments", "12")));
    }

    @Test
    public void serialization_empty() throws Exception {
        assertThat(TestUtil.moshi(Article.class).adapter(Article.class).toJson(new Article()), equalTo("{\"type\":\"articles\"}"));
    }

    @Test
    public void serialization_attributes() throws Exception {
        Article article = new Article();
        article.setTitle("It sucks!");
        article.setIgnored("should be ok to set");
        assertThat(TestUtil.moshi(Article.class).adapter(Article.class).toJson(article), equalTo(
                "{\"type\":\"articles\",\"attributes\":{\"title\":\"It sucks!\"}}"));
    }

    @Test
    public void serialization_relationships() throws Exception {
        Article article = new Article();
        article.setAuthor(new HasOne<Person>(new ResourceIdentifier("people", "2")));
        assertThat(TestUtil.moshi(Article.class).adapter(Article.class).toJson(article), equalTo(
                "{\"type\":\"articles\",\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"2\"}}}}"));
    }

    @Test
    public void deserialization_pojo() throws Exception {
        PlainObject article = TestUtil.moshi(PlainObject.class).adapter(PlainObject.class).fromJson(JSON);
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getType(), equalTo("articles"));
        assertThat(article.title, equalTo("JSON API paints my bikeshed!"));
        assertThat(article.ignored, nullValue());
        assertThat(article.author.get(), equalTo(new ResourceIdentifier("people", "9")));
        assertThat(article.comments.get(), hasItems(
                new ResourceIdentifier("comments", "5"),
                new ResourceIdentifier("comments", "12")));
    }

    @Test
    public void serialization_pojo() throws Exception {
        PlainObject article = new PlainObject();
        article.title = "It sucks!";
        article.author = new HasOne<Person>(new ResourceIdentifier("people", "2"));
        assertThat(TestUtil.moshi(PlainObject.class).adapter(PlainObject.class).toJson(article), equalTo(
                "{\"type\":\"articles\",\"attributes\":{\"title\":\"It sucks!\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"2\"}}}}"));
    }

}
