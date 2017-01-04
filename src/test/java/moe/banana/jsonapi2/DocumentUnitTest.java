package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import com.squareup.moshi.Types;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import moe.banana.jsonapi2.model.Person;
import moe.banana.jsonapi2.model.Photo;
import moe.banana.jsonapi2.model.Photo2;

import java.io.EOFException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
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

    @JsonApi(type = "default")
    public static class Default extends Resource { }

    public static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .add(Person.class)
                .add(Comment.class)
                .add(Photo.class)
                .add(Default.class)
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
                .build());
        return builder.build();
    }

    @Test(expected = EOFException.class)
    public void deserialize_empty_document() throws Exception {
        assertNull(getDocumentAdapter(moshi(), Article.class).fromJson(""));
    }

    @Test
    public void deserialize_array_of_object() throws Exception {
        Document<Article> articles = getDocumentAdapter(moshi(), Article.class).fromJson(JSON_DATA_1);
        assertThat(articles, notNullValue());
        assertThat(articles.size(), equalTo(1));
        Article a = articles.get(0);
        assertThat(a.getId(), equalTo("1"));
        assertThat(a.getType(), equalTo("articles"));
        assertThat(a.title, equalTo("JSON API paints my bikeshed!"));
        assertThat(a.author.get(articles).firstName, equalTo("Dan"));
        assertThat(a.comments.get(articles).size(), equalTo(2));
    }

    @Test
    public void deserialize_linkage_not_found() throws Exception {
        Document<Article> articles = getDocumentAdapter(moshi(), Article.class).fromJson(JSON_DATA_1);
        List<Comment> comments = articles.get(0).comments.get(articles);
        assertThat(comments.size(), equalTo(2));
        assertNull(comments.get(0).author.get(articles));
    }

    @Test
    public void deserialize_linkage_fallback() throws Exception {
        Document<Article> articleDocument = getDocumentAdapter(moshi(), Article.class).fromJson(JSON_DATA_1);
        List<Comment> comments = articleDocument.get(0).comments.get(articleDocument);
        assertThat(comments.size(), equalTo(2));
        Person defaultAuthor = new Person();
        assertEquals(comments.get(0).author.get(articleDocument, defaultAuthor), defaultAuthor);
        assertNotEquals(comments.get(1).author.get(articleDocument, defaultAuthor), defaultAuthor);
    }

    @Test
    public void deserialize_object() throws Exception {
        Article article = getDocumentAdapter(moshi(), Article.class).fromJson(JSON_DATA_2).get();
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getType(), equalTo("articles"));
        assertThat(article.title, equalTo("JSON API paints my bikeshed!"));
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_strictly() throws Exception {
        getDocumentAdapter(strictMoshi(), Article.class).fromJson(JSON_DATA_2);
    }

    @Test
    public void deserialize_polymorphic_object() throws Exception {
        assertThat(getDocumentAdapter(moshi(), Resource.class).fromJson(JSON_DATA_2).get(), instanceOf(Article.class));
    }

    @Test
    public void deserialize_unknown_polymorphic_object() throws Exception {
        Resource resource = getDocumentAdapter(moshi(), Resource.class).fromJson(JSON_DATA_3).get();
        assertThat(resource, instanceOf(Default.class));
        assertThat(resource.getId(), equalTo("1"));
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_unknown_polymorphic_object_strictly() throws Exception {
        getDocumentAdapter(strictMoshi(), Resource.class).fromJson(JSON_DATA_3);
    }

    @Test
    public void deserialize_polymorphic_array() throws Exception {
        Document<Resource> document = getDocumentAdapter(moshi(), Resource.class).fromJson(JSON_DATA_4);
        assertThat(document.get(0), instanceOf(Article.class));
        assertThat(document.get(1), instanceOf(Photo.class));
    }

    @Test
    public void deserialize_polymorphic_priority() throws Exception {
        Moshi moshi = new Moshi.Builder()
                .add(ResourceAdapterFactory.builder()
                        .add(Photo.class)
                        .add(Photo2.class)
                        .add(Default.class)
                        .build())
                .build();
        Document<Resource> document = getDocumentAdapter(moshi, Resource.class).fromJson(JSON_DATA_4);
        assertThat(document.get(1), instanceOf(Photo2.class));
    }

    @Test
    public void serialize_object() throws Exception {
        Person author = new Person();
        author.setId("5");
        author.firstName = "George";
        author.lastName = "Orwell";
        Comment comment1 = new Comment();
        comment1.setId("1");
        comment1.body = "Awesome!";
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = new HasOne(author);
        article.comments = new HasMany<>(comment1);
        Document document = new Document();
        document.set(article);
        document.include(author);
        document.include(comment1);
        assertThat(getDocumentAdapter(moshi(), Article.class).toJson(document), equalTo(
                "{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}},\"included\":[{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}},{\"type\":\"comments\",\"id\":\"1\",\"attributes\":{\"body\":\"Awesome!\"}}]}"));
    }

    @Test
    public void serialize_array_of_object() throws Exception {
        Document document = new Document();
        Person author = new Person();
        author.setId("5");
        author.firstName = "George";
        author.lastName = "Orwell";
        document.include(author);
        Comment comment1 = new Comment();
        comment1.setId("1");
        comment1.body = "Awesome!";
        document.include(comment1);
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = new HasOne<>( author);
        article.comments = new HasMany<>(comment1);
        document.add(article);
        assertThat(document.included.get(0), instanceOf(Person.class));
        assertThat(document.included.get(1), instanceOf(Comment.class));
        assertThat(
                getDocumentAdapter(moshi(), Article.class).toJson(document),
                equalTo("{\"data\":[{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}],\"included\":[{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}},{\"type\":\"comments\",\"id\":\"1\",\"attributes\":{\"body\":\"Awesome!\"}}]}"));
    }

    @Test
    public void serialize_polymorphic_object() throws Exception {
        Document document = new Document();
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        document.set(article);
        assertThat(
                getDocumentAdapter(moshi(), Resource.class).toJson(document),
                equalTo("{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"}}}"));
    }

    @Test
    public void serialize_array_of_polymorphic_object() throws Exception {
        Person author = new Person();
        author.setId("5");
        author.firstName = "George";
        author.lastName = "Orwell";
        Article article = new Article();
        article.title = "Nineteen Eighty-Four";
        article.author = new HasOne<>(author);
        Document document = new Document();
        document.add(article);
        document.add(author);
        assertThat(
                getDocumentAdapter(moshi(), Resource.class).toJson(document),
                equalTo("{\"data\":[" +
                        "{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}}}}," +
                        "{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}}" +
                        "]}"));
    }

    @Test
    public void serialize_array_of_resource_identifiers() throws Exception {
        Document document = new Document();
        document.add(new ResourceIdentifier("people", "5"));
        document.add(new ResourceIdentifier("people", "11"));
        assertThat(getDocumentAdapter(moshi(), ResourceIdentifier.class).toJson(document),
                equalTo("{\"data\":[{\"type\":\"people\",\"id\":\"5\"},{\"type\":\"people\",\"id\":\"11\"}]}"));
    }

    @Test
    public void deserialize_array_of_resource_identifiers() throws Exception {
        Document document = getDocumentAdapter(moshi(), ResourceIdentifier.class).fromJson("{\"data\":[{\"type\":\"people\",\"id\":\"5\"},{\"type\":\"people\",\"id\":\"11\"}]}");
        assertThat(document.size(), equalTo(2));
        assertThat(document.get(0), instanceOf(ResourceIdentifier.class));
        assertThat(document.get(1).getType(), equalTo("people"));
    }

    @Test
    public void serialize_resource_identifiers() throws Exception {
        Document document = new Document();
        document.set(new ResourceIdentifier("people", "5"));
        assertThat(getDocumentAdapter(moshi(), ResourceIdentifier.class).toJson(document),
                equalTo("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}"));
    }

    @Test
    public void deserialize_resource_identifiers() throws Exception {
        Document document = getDocumentAdapter(moshi(), ResourceIdentifier.class).fromJson("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}");
        assertThat(document.get(), instanceOf(ResourceIdentifier.class));
        assertThat(document.get().getId(), equalTo("5"));
    }

    @Test
    public void serialize_empty_document() throws Exception {
        Document document = new Document();
        assertThat(getDocumentAdapter(moshi(), ResourceIdentifier.class).toJson(document), equalTo("{\"data\":null}"));
        assertThat(getDocumentAdapter(moshi(), ResourceIdentifier.class).toJson(document.asList()), equalTo("{\"data\":[]}"));
    }

    @Test
    public void equals() throws Exception {
        Article a = getDocumentAdapter(moshi(), Article.class).fromJson(JSON_DATA_2).get();
        Article b = new Article();
        assertNotEquals(b, a);
        assertEquals(b, new Article());
        assertNotEquals(b, null);
        b.setId(a.getId());
        assertEquals(b, a);
        assertEquals(b.hashCode(), a.hashCode());
    }

    public <T extends ResourceIdentifier> JsonAdapter<Document<T>> getDocumentAdapter(Moshi moshi, Class<T> type) {
        return moshi.adapter(Types.newParameterizedType(Document.class, type));
    }

}
