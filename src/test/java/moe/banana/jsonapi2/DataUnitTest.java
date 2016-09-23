package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;
import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import moe.banana.jsonapi2.model.Person;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataUnitTest {

    private static final String JSON_DATA_1 = "[" +
            "{" +
            "  \"id\": \"1\"," +
            "  \"type\": \"articles\"," +
            "  \"attributes\": {" +
            "    \"title\": \"JSON API paints my bikeshed!\"" +
            "  }," +
            "  \"relationships\": {" +
            "    \"author\": {" +
            "      \"links\": {" +
            "        \"self\": \"http://example.com/articles/1/relationships/author\"," +
            "        \"related\": \"http://example.com/articles/1/author\"" +
            "      }," +
            "      \"data\": { \"type\": \"people\", \"id\": \"9\" }" +
            "    }," +
            "    \"comments\": {" +
            "      \"links\": {" +
            "        \"self\": \"http://example.com/articles/1/relationships/comments\"," +
            "        \"related\": \"http://example.com/articles/1/comments\"" +
            "      }," +
            "      \"data\": [" +
            "        { \"type\": \"comments\", \"id\": \"5\" }," +
            "        { \"type\": \"comments\", \"id\": \"12\" }" +
            "      ]" +
            "    }" +
            "  }," +
            "  \"links\": {" +
            "    \"self\": \"http://example.com/articles/1\"" +
            "  }" +
            "}]";

    private static final String JSON_DATA_2 = "{" +
            "  \"type\": \"articles\"," +
            "  \"id\": \"1\"," +
            "  \"attributes\": {" +
            "    \"title\": \"JSON API paints my bikeshed!\"" +
            "  }" +
            "}";

    public static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .add(Person.class)
                .add(Comment.class)
                .build());
        return builder.build();
    }

    @Test
    public void deserialize_array_of_object() throws Exception {
        Article[] articles = moshi().adapter(Article[].class).fromJson(JSON_DATA_1);
        assertThat(articles, notNullValue());
        assertThat(articles.length, equalTo(1));
        Article a = articles[0];
        assertThat(a.getId(), equalTo("1"));
        assertThat(a.getType(), equalTo("articles"));
        assertThat(a.title, equalTo("JSON API paints my bikeshed!"));
    }

    @Test
    public void deserialize_object() throws Exception {
        Article article = moshi().adapter(Article.class).fromJson(JSON_DATA_2);
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getType(), equalTo("articles"));
        assertThat(article.title, equalTo("JSON API paints my bikeshed!"));
    }

    @Test
    public void serialize_object() throws Exception {
        Document document = Document.create();
        Person author = new Person();
        author.setId("5");
        author.firstName = "George";
        author.lastName = "Orwell";
        document.addInclude(author);
        Comment comment1 = new Comment();
        comment1.setId("1");
        comment1.body = "Awesome!";
        document.addInclude(comment1);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = HasOne.create(article, author);
        article.comments = HasMany.create(article, comment1);
        assertThat(moshi().adapter(Article.class).toJson(article), equalTo("{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}"));
    }

    @Test
    public void serialize_array_of_object() throws Exception {
        Document document = Document.create();
        Person author = new Person();
        author.setId("5");
        author.firstName = "George";
        author.lastName = "Orwell";
        document.addInclude(author);
        Comment comment1 = new Comment();
        comment1.setId("1");
        comment1.body = "Awesome!";
        document.addInclude(comment1);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = HasOne.create(article, author);
        article.comments = HasMany.create(article, comment1);
        assertThat(document.included.get(0), instanceOf(Person.class));
        assertThat(document.included.get(1), instanceOf(Comment.class));
        assertThat(
                moshi().adapter(Article[].class).toJson(new Article[] { article }),
                equalTo("[{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}]"));
    }

}
