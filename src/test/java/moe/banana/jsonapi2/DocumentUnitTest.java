package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;
import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import moe.banana.jsonapi2.model.Person;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@SuppressWarnings("ALL")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DocumentUnitTest {

    private static final String JSON = "{" +
            "  \"links\": {" +
            "    \"self\": \"http://example.com/articles\"," +
            "    \"next\": \"http://example.com/articles?page[offset]=2\"," +
            "    \"last\": \"http://example.com/articles?page[offset]=10\"" +
            "  }," +
            "  \"data\": [{" +
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
            "  }]" +
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
    public void deserialization() throws Exception {

    }

    @Test
    public void serialization() throws Exception {

    }

    @Test
    public void linkage_dereference() throws Exception {

    }

}
