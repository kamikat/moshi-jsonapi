package moe.banana.jsonapi2;

import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import moe.banana.jsonapi2.model.Person;
import moe.banana.jsonapi2.model.Photo;
import moe.banana.jsonapi2.model.Photo2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentUnitTest {

    private static final String JSON_DATA_1 = "{" +
            "  \"links\": {" +
            "    \"self\": \"http://example.com/articles\"," +
            "    \"next\": \"http://example.com/articles?page[offset]=2\"," +
            "    \"last\": \"http://example.com/articles?page[offset]=10\"" +
            "  }," +
            "  \"data\": [{" +
            "    \"id\": \"1\"," +
            "    \"type\": \"articles\"," +
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
            "  }]," +
            "  \"included\": [{" +
            "    \"type\": \"people\"," +
            "    \"id\": \"9\"," +
            "    \"attributes\": {" +
            "      \"first-name\": \"Dan\"," +
            "      \"last-name\": \"Gebhardt\"," +
            "      \"twitter\": \"dgeb\"," +
            "      \"age\": 20" +
            "    }," +
            "    \"links\": {" +
            "      \"self\": \"http://example.com/people/9\"" +
            "    }" +
            "  }, {" +
            "    \"type\": \"comments\"," +
            "    \"id\": \"5\"," +
            "    \"attributes\": {" +
            "      \"body\": \"First!\"" +
            "    }," +
            "    \"relationships\": {" +
            "      \"author\": {" +
            "        \"data\": { \"type\": \"people\", \"id\": \"2\" }" +
            "      }" +
            "    }," +
            "    \"links\": {" +
            "      \"self\": \"http://example.com/comments/5\"" +
            "    }" +
            "  }, {" +
            "    \"type\": \"comments\"," +
            "    \"id\": \"12\"," +
            "    \"attributes\": {" +
            "      \"body\": \"I like XML better\"" +
            "    }," +
            "    \"relationships\": {" +
            "      \"author\": {" +
            "        \"data\": { \"type\": \"people\", \"id\": \"9\" }" +
            "      }" +
            "    }," +
            "    \"links\": {" +
            "      \"self\": \"http://example.com/comments/12\"" +
            "    }" +
            "  }, {" +
            "    \"type\": \"aliens\"," +
            "    \"id\": \"9\"," +
            "    \"attributes\": {" +
            "      \"first-name\": \"Dan\"," +
            "      \"last-name\": \"Gebhardt\"," +
            "      \"twitter\": \"dgeb\"," +
            "      \"age\": 20" +
            "    }" +
            "  }]" +
            "}";

    private static final String JSON_DATA_2 = "{" +
            "  \"data\": {" +
            "    \"type\": \"articles\"," +
            "    \"id\": \"1\"," +
            "    \"attributes\": {" +
            "      \"title\": \"JSON API paints my bikeshed!\"" +
            "    }" +
            "  }," +
            "  \"included\": [{" +
            "    \"type\": \"unidentified\"," +
            "    \"id\": \"9\"," +
            "    \"attributes\": {" +
            "      \"first-name\": \"Dan\"," +
            "      \"last-name\": \"Gebhardt\"," +
            "      \"twitter\": \"dgeb\"," +
            "      \"age\": 20" +
            "    }" +
            "  }]" +
            "}";

    private static final String JSON_DATA_3 = "{" +
            "  \"data\": {" +
            "    \"type\": \"unidentified\"," +
            "    \"id\": \"1\"," +
            "    \"attributes\": {" +
            "      \"title\": \"JSON API paints my bikeshed!\"" +
            "    }" +
            "  }" +
            "}";

    private static final String JSON_DATA_4 = "{" +
            "  \"data\": [{" +
            "    \"type\": \"articles\"," +
            "    \"id\": \"5\"," +
            "    \"attributes\": {" +
            "      \"body\": \"First!\"" +
            "    }," +
            "    \"relationships\": {" +
            "      \"author\": {" +
            "        \"data\": { \"type\": \"people\", \"id\": \"2\" }" +
            "      }" +
            "    }" +
            "  }, {" +
            "    \"type\": \"photos\"," +
            "    \"id\": \"12\"," +
            "    \"attributes\": {" +
            "      \"url\": \"http://...\"," +
            "      \"visible\": false," +
            "      \"shutter\": 0.5," +
            "      \"longitude\": null," +
            "      \"latitude\": null" +
            "    }," +
            "    \"relationships\": {" +
            "      \"author\": {" +
            "        \"data\": { \"type\": \"people\", \"id\": \"9\" }" +
            "      }" +
            "    }" +
            "  }]" +
            "}";

    public static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .add(Person.class)
                .add(Comment.class)
                .add(Photo.class)
                .build());
        return builder.build();
    }

    public static Moshi strictMoshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .add(Person.class)
                .add(Comment.class)
                .add(Photo.class)
                .strict()
                .build());
        return builder.build();
    }

    @Test
    public void deserialize_empty_document() throws Exception {
        assertThat(moshi().adapter(Article[].class).fromJson(""), nullValue());
        assertThat(moshi().adapter(Article.class).fromJson(""), nullValue());
    }

    @Test
    public void deserialize_array_of_object() throws Exception {
        Article[] articles = moshi().adapter(Article[].class).fromJson(JSON_DATA_1);
        assertThat(articles, notNullValue());
        assertThat(articles.length, equalTo(1));
        Article a = articles[0];
        assertThat(a._id, equalTo("1"));
        assertThat(a._type, equalTo("articles"));
        assertThat(a.title, equalTo("JSON API paints my bikeshed!"));
        assertThat(a.author.get().firstName, equalTo("Dan"));
        assertThat(a.comments.getAll().length, equalTo(2));
    }

    @Test
    public void deserialize_linkage_not_found() throws Exception {
        Article[] articles = moshi().adapter(Article[].class).fromJson(JSON_DATA_1);
        Comment[] comments = articles[0].comments.getAll();
        assertThat(comments.length, equalTo(2));
        assertThat(comments[0].author.get(), nullValue());
    }

    @Test
    public void deserialize_linkage_fallback() throws Exception {
        Article[] articles = moshi().adapter(Article[].class).fromJson(JSON_DATA_1);
        Comment[] comments = articles[0].comments.getAll();
        assertThat(comments.length, equalTo(2));
        Person defaultAuthor = new Person();
        assertTrue(comments[0].author.get(defaultAuthor) == defaultAuthor);
        assertTrue(comments[1].author.get(defaultAuthor) != defaultAuthor);
    }

    @Test
    public void deserialize_object() throws Exception {
        Article article = moshi().adapter(Article.class).fromJson(JSON_DATA_2);
        assertThat(article._id, equalTo("1"));
        assertThat(article._type, equalTo("articles"));
        assertThat(article.title, equalTo("JSON API paints my bikeshed!"));
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_strictly() throws Exception {
        strictMoshi().adapter(Article.class).fromJson(JSON_DATA_2);
    }

    @Test
    public void deserialize_polymorphic_object() throws Exception {
        assertThat(moshi().adapter(Resource.class).fromJson(JSON_DATA_2), instanceOf(Article.class));
    }

    @Test
    public void deserialize_unknown_polymorphic_object() throws Exception {
        Resource res = moshi().adapter(Resource.class).fromJson(JSON_DATA_3);
        assertThat(res, instanceOf(Resource.class));
        assertThat(res._id, equalTo("1"));
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_unknown_polymorphic_object_strictly() throws Exception {
        strictMoshi().adapter(Resource.class).fromJson(JSON_DATA_3);
    }

    @Test
    public void deserialize_polymorphic_array() throws Exception {
        Resource[] resources = moshi().adapter(Resource[].class).fromJson(JSON_DATA_4);
        assertThat(resources[0], instanceOf(Article.class));
        assertThat(resources[1], instanceOf(Photo.class));
    }

    @Test
    public void deserialize_polymorphic_priority() throws Exception {
        Moshi moshi = new Moshi.Builder()
                .add(ResourceAdapterFactory.builder()
                        .add(Photo.class)
                        .add(Photo2.class)
                        .build())
                .build();
        Resource[] resources = moshi.adapter(Resource[].class).fromJson(JSON_DATA_4);
        assertThat(resources[1], instanceOf(Photo2.class));
    }

    @Test
    public void serialize_object() throws Exception {
        Document document = Document.create();
        Person author = new Person();
        author._id = "5";
        author.firstName = "George";
        author.lastName = "Orwell";
        author.includeBy(document);
        Comment comment1 = new Comment();
        comment1._id = "1";
        comment1.body = "Awesome!";
        comment1.includeBy(document);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = HasOne.create(article, author);
        article.comments = HasMany.create(article, comment1);
        article.addTo(document);
        assertThat(moshi().adapter(Article.class).toJson(article), equalTo("{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}},\"included\":[{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}},{\"type\":\"comments\",\"id\":\"1\",\"attributes\":{\"body\":\"Awesome!\"}}]}"));
    }

    @Test
    public void serialize_array_of_object() throws Exception {
        Document document = Document.create();
        Person author = new Person();
        author._id = "5";
        author.firstName = "George";
        author.lastName = "Orwell";
        author.includeBy(document);
        Comment comment1 = new Comment();
        comment1._id = "1";
        comment1.body = "Awesome!";
        comment1.includeBy(document);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = HasOne.create(article, author);
        article.comments = HasMany.create(article, comment1);
        article.addTo(document);
        assertThat(document.included.get(0), instanceOf(Person.class));
        assertThat(document.included.get(1), instanceOf(Comment.class));
        assertThat(
                moshi().adapter(Article[].class).toJson(new Article[]{article}),
                equalTo("{\"data\":[{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}],\"included\":[{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}},{\"type\":\"comments\",\"id\":\"1\",\"attributes\":{\"body\":\"Awesome!\"}}]}"));
    }

    @Test
    public void serialize_polymorphic_object() throws Exception {
        Document document = Document.create();
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.addTo(document);
        assertThat(
                moshi().adapter(Resource.class).toJson(article),
                equalTo("{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"}}}"));
    }

    @Test
    public void serialize_array_of_polymorphic_object() throws Exception {
        Document document = Document.create();
        Person author = new Person();
        author._id = "5";
        author.firstName = "George";
        author.lastName = "Orwell";
        author.addTo(document);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = HasOne.create(article, author);
        article.addTo(document);
        assertThat(
                moshi().adapter(Resource[].class).toJson(new Resource[]{article, author}),
                equalTo("{\"data\":[" +
                        "{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}}}}," +
                        "{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}}" +
                        "]}"));
    }

    @Test
    public void equals() throws Exception {
        Article a = moshi().adapter(Article.class).fromJson(JSON_DATA_2);
        Article b = new Article();
        assertThat(b.equals(a), is(false));
        assertThat(b.equals(new Article()), is(true));
        assertThat(b.equals(null), is(false));
        b._id = a._id;
        assertThat(b.equals(a), is(true));
        assertThat(b.hashCode(), equalTo(a.hashCode()));
    }

}
