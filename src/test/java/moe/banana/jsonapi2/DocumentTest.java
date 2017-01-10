package moe.banana.jsonapi2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import moe.banana.jsonapi2.model.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.EOFException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
public class DocumentTest {

    @Test(expected = EOFException.class)
    public void deserialize_empty() throws Exception {
        getDocumentAdapter(Article.class).fromJson("");
    }

    @Test
    public void deserialize_object() throws Exception {
        Document<Article> document = getDocumentAdapter(Article.class).fromJson(TestUtil.fromResource("/single.json"));
        assertFalse(document.isList());
        assertOnArticle1(document.get());
    }

    @Test
    public void deserialize_private_type() throws Exception {
        assertOnArticle1(getDocumentAdapter(Article2.class).fromJson(TestUtil.fromResource("/single.json")).get());
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_no_default() throws Exception {
        TestUtil.moshi(true, Article.class)
                .adapter(Types.newParameterizedType(Document.class, Article.class))
                .fromJson(TestUtil.fromResource("/multiple_compound.json"));
    }

    @Test
    public void deserialize_polymorphic_type() throws Exception {
        Resource resource = getDocumentAdapter(Resource.class, Article.class).fromJson(TestUtil.fromResource("/single.json")).get();
        assertThat(resource, instanceOf(Article.class));
        assertOnArticle1(((Article) resource));
    }

    @Test
    public void deserialize_polymorphic_fallback() throws Exception {
        Resource resource = getDocumentAdapter(Resource.class).fromJson(TestUtil.fromResource("/single.json")).get();
        assertThat(resource.getId(), equalTo("1"));
        assertThat(resource, instanceOf(TestUtil.Default.class));
    }

    @Test
    public void deserialize_multiple_objects() throws Exception {
        Document<Article> document = getDocumentAdapter(Article.class).fromJson(TestUtil.fromResource("/multiple_compound.json"));
        assertThat(document, notNullValue());
        assertThat(document.size(), equalTo(1));
        assertTrue(document.isList());
        assertOnArticle1(document.get(0));
    }

    @Test
    public void deserialize_multiple_polymorphic() throws Exception {
        Document<Resource> document = getDocumentAdapter(Resource.class, Article.class, Photo.class).fromJson(TestUtil.fromResource("/multiple_polymorphic.json"));
        assertThat(document.get(0), instanceOf(Article.class));
        assertThat(document.get(1), instanceOf(Photo.class));
        assertOnArticle1((Article) document.get(0));
    }

    @Test
    public void deserialize_multiple_polymorphic_type_priority() throws Exception {
        Document<Resource> document = getDocumentAdapter(Resource.class, Photo.class, Photo2.class).fromJson(TestUtil.fromResource("/multiple_polymorphic.json"));
        assertThat(document.get(1), instanceOf(Photo2.class));
    }

    @Test(expected = JsonDataException.class)
    public void deserialize_multiple_polymorphic_no_default() throws Exception {
        TestUtil.moshi(true, Article.class)
                .adapter(Types.newParameterizedType(Document.class, Resource.class))
                .fromJson(TestUtil.fromResource("/multiple_polymorphic.json"));
    }

    @Test
    public void deserialize_unparameterized() throws Exception {
        Document document = getDocumentAdapter(null, Person.class).fromJson("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}");
        assertFalse(document.isList());
        assertThat(document.get().getType(), equalTo("people"));
        assertThat(document.get(), instanceOf(Person.class));
    }

    @Test
    public void serialize_empty() throws Exception {
        Document document = new Document();
        assertThat(getDocumentAdapter(ResourceIdentifier.class).toJson(document), equalTo("{\"data\":null}"));
        assertThat(getDocumentAdapter(ResourceIdentifier.class).toJson(document.asList()), equalTo("{\"data\":[]}"));
    }

    @Test
    public void serialize_object() throws Exception {
        Article article = new Article();
        article.setTitle("Nineteen Eighty-Four");
        article.setAuthor(new HasOne<Person>("people", "5"));
        article.setComments(new HasMany<Comment>(
                new ResourceIdentifier("comments", "1")));
        Document document = new Document();
        document.set(article);
        assertThat(getDocumentAdapter(Article.class).toJson(document), equalTo(
                "{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}}"));
    }

    @Test
    public void serialize_polymorphic() throws Exception {
        Article article = new Article();
        article.setTitle("Nineteen Eighty-Four");
        Document document = new Document();
        document.set(article);
        assertThat(getDocumentAdapter(Resource.class).toJson(document),
                equalTo("{\"data\":{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"}}}"));
    }

    @Test
    public void serialize_multiple_polymorphic_compound() throws Exception {
        Document document = new Document();
        Comment comment1 = new Comment();
        comment1.setId("1");
        comment1.setBody("Awesome!");
        Person author = new Person();
        author.setId("5");
        author.setFirstName("George");
        author.setLastName("Orwell");
        Article article = new Article();
        article.setTitle("Nineteen Eighty-Four");
        article.setAuthor(new HasOne<Person>(author));
        article.setComments(new HasMany<Comment>(comment1));
        document.add(article);
        document.add(author);
        document.include(comment1);
        assertThat(getDocumentAdapter(Resource.class).toJson(document),
                equalTo("{\"data\":[" +
                        "{\"type\":\"articles\",\"attributes\":{\"title\":\"Nineteen Eighty-Four\"},\"relationships\":{\"author\":{\"data\":{\"type\":\"people\",\"id\":\"5\"}},\"comments\":{\"data\":[{\"type\":\"comments\",\"id\":\"1\"}]}}}," +
                        "{\"type\":\"people\",\"id\":\"5\",\"attributes\":{\"first-name\":\"George\",\"last-name\":\"Orwell\"}}" +
                        "],\"included\":[{\"type\":\"comments\",\"id\":\"1\",\"attributes\":{\"body\":\"Awesome!\"}}]}"));
    }

    @Test
    public void deserialize_resource_identifier() throws Exception {
        Document document = getDocumentAdapter(ResourceIdentifier.class)
                .fromJson("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}");
        assertThat(document.get(), instanceOf(ResourceIdentifier.class));
        assertThat(document.get().getId(), equalTo("5"));
    }

    @Test
    public void deserialize_multiple_resource_identifiers() throws Exception {
        Document document = getDocumentAdapter(ResourceIdentifier.class)
                .fromJson("{\"data\":[{\"type\":\"people\",\"id\":\"5\"},{\"type\":\"people\",\"id\":\"11\"}]}");
        assertThat(document.size(), equalTo(2));
        assertThat(document.get(0), instanceOf(ResourceIdentifier.class));
        assertThat(document.get(1).getType(), equalTo("people"));
    }

    @Test
    public void serialize_resource_identifier() throws Exception {
        Document document = new Document();
        document.set(new ResourceIdentifier("people", "5"));
        assertThat(getDocumentAdapter(ResourceIdentifier.class).toJson(document),
                equalTo("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}"));
    }

    @Test
    public void serialize_multiple_resource_identifiers() throws Exception {
        Document document = new Document();
        document.add(new ResourceIdentifier("people", "5"));
        document.add(new ResourceIdentifier("people", "11"));
        assertThat(getDocumentAdapter(ResourceIdentifier.class).toJson(document),
                equalTo("{\"data\":[{\"type\":\"people\",\"id\":\"5\"},{\"type\":\"people\",\"id\":\"11\"}]}"));
    }

    @JsonApi(type = "articles")
    private static class Article2 extends Article {

    }

    public <T extends ResourceIdentifier> JsonAdapter<Document<T>> getDocumentAdapter(Class<T> typeParameter,
                                                                                      Class<? extends Resource>... knownTypes) {
        Moshi moshi;
        if (typeParameter == null) {
            return (JsonAdapter) TestUtil.moshi(knownTypes).adapter(Document.class);
        } else if (typeParameter.getAnnotation(JsonApi.class) != null) {
            Class<? extends Resource>[] types = new Class[knownTypes.length + 1];
            types[0] = (Class) typeParameter;
            for (int i = 0; i != knownTypes.length; i++) {
                types[i + 1] = knownTypes[i];
            }
            moshi = TestUtil.moshi(types);
        } else {
            moshi = TestUtil.moshi(knownTypes);
        }
        return moshi.adapter(Types.newParameterizedType(Document.class, typeParameter));
    }

    private void assertOnArticle1(Article article) {
        assertThat(article.getId(), equalTo("1"));
        assertThat(article.getType(), equalTo("articles"));
        assertThat(article.getTitle(), equalTo("JSON API paints my bikeshed!"));
        assertThat(article.getAuthor().get(), equalTo(
                new ResourceIdentifier("people", "9")));
        assertThat(article.getComments().get(), hasItems(
                new ResourceIdentifier("comments", "5"),
                new ResourceIdentifier("comments", "12")));
    }

}
