package moe.banana.jsonapi.test;

import com.squareup.moshi.Moshi;
import moe.banana.jsonapi.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ALL")
public class JsonApiUnitTest {

    private static final String JSON = "{\n" +
            "  \"links\": {\n" +
            "    \"self\": \"http://example.com/articles\",\n" +
            "    \"next\": \"http://example.com/articles?page[offset]=2\",\n" +
            "    \"last\": \"http://example.com/articles?page[offset]=10\"\n" +
            "  },\n" +
            "  \"data\": [{\n" +
            "    \"type\": \"articles\",\n" +
            "    \"id\": \"1\",\n" +
            "    \"attributes\": {\n" +
            "      \"title\": \"JSON API paints my bikeshed!\"\n" +
            "    },\n" +
            "    \"relationships\": {\n" +
            "      \"author\": {\n" +
            "        \"links\": {\n" +
            "          \"self\": \"http://example.com/articles/1/relationships/author\",\n" +
            "          \"related\": \"http://example.com/articles/1/author\"\n" +
            "        },\n" +
            "        \"data\": { \"type\": \"people\", \"id\": \"9\" }\n" +
            "      },\n" +
            "      \"comments\": {\n" +
            "        \"links\": {\n" +
            "          \"self\": \"http://example.com/articles/1/relationships/comments\",\n" +
            "          \"related\": \"http://example.com/articles/1/comments\"\n" +
            "        },\n" +
            "        \"data\": [\n" +
            "          { \"type\": \"comments\", \"id\": \"5\" },\n" +
            "          { \"type\": \"comments\", \"id\": \"12\" }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": \"http://example.com/articles/1\"\n" +
            "    }\n" +
            "  }],\n" +
            "  \"included\": [{\n" +
            "    \"type\": \"people\",\n" +
            "    \"id\": \"9\",\n" +
            "    \"attributes\": {\n" +
            "      \"first-name\": \"Dan\",\n" +
            "      \"last-name\": \"Gebhardt\",\n" +
            "      \"twitter\": \"dgeb\"\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": \"http://example.com/people/9\"\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"type\": \"comments\",\n" +
            "    \"id\": \"5\",\n" +
            "    \"attributes\": {\n" +
            "      \"body\": \"First!\"\n" +
            "    },\n" +
            "    \"relationships\": {\n" +
            "      \"author\": {\n" +
            "        \"data\": { \"type\": \"people\", \"id\": \"2\" }\n" +
            "      }\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": \"http://example.com/comments/5\"\n" +
            "    }\n" +
            "  }, {\n" +
            "    \"type\": \"comments\",\n" +
            "    \"id\": \"12\",\n" +
            "    \"attributes\": {\n" +
            "      \"body\": \"I like XML better\"\n" +
            "    },\n" +
            "    \"relationships\": {\n" +
            "      \"author\": {\n" +
            "        \"data\": { \"type\": \"people\", \"id\": \"9\" }\n" +
            "      }\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": \"http://example.com/comments/12\"\n" +
            "    }\n" +
            "  }]\n" +
            "}";

    public static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(new JsonApiAdapterFactory());
        builder.add(new ResourceJsonAdapter.Factory(Article.class, Comment.class, People.class));
        builder.add(new TestAdapterFactory());
        return builder.build();
    }

    @Test
    public void jsonApiDocument_deserialization() throws Exception {
        Document document = moshi().adapter(Document.class).fromJson(JSON);
        assertThat(document.jsonapi(), is(nullValue()));
        assertThat(document.links().last().href(), is("http://example.com/articles?page[offset]=10"));
        assertThat(document.links().self().href(), is("http://example.com/articles"));
        assertThat(document.data().one(), is(false));
        assertThat(document.data().size(), is(1));
        assertThat(document.data().get(0).relationships().size(), is(2));
        assertThat(document.data().get(0), is(document.data().only()));
        assertThat(document.data().only().relationships().get("author").data().type(), is("people"));
        assertThat(document.data().only().relationships().get("comments").data().one(), is(false));
        assertThat(document.included(), is(notNullValue()));
        assertThat(document.included().size(), is(3));
        System.out.println(document);
    }

    @Test
    public void jsonApiResource_serialization() throws Exception {
        Resource.Builder builder = Resource.builder()
                .id("1_generated")
                .type(Resource.typeOf(Article.class))
                .attributes(new AutoValue_Article("creation"))
                .relationships(
                        Maps.builder()
                                .key("author")
                                .val(Relationship.builder()
                                        .data(ResourceLinkage.create(Resource.typeOf(Article.class), "10"))
                                        .links(Relationship.Links.builder()
                                                .self(Link.create("http://example.com/articles/1/relationships/author"))
                                                .related(Link.create("http://example.com/articles/1/author"))
                                                .build())
                                        .build())
                                .build())
                .links(Links.create("http://example.com/articles/1_generated"));
        String json = moshi().adapter(Resource.class).toJson(builder.build());
        assertThat(json, equalTo("{\"type\":\"articles\",\"id\":\"1_generated\",\"attributes\":{\"title\":\"creation\"},\"relationships\":{\"author\":{\"links\":{\"self\":\"http://example.com/articles/1/relationships/author\",\"related\":\"http://example.com/articles/1/author\"},\"data\":{\"type\":\"articles\",\"id\":\"10\"}}},\"links\":{\"self\":\"http://example.com/articles/1_generated\"}}"));
        System.out.println(json);
    }

}
