package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.util.List;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Comment;
import moe.banana.jsonapi2.model.Person;
import moe.banana.jsonapi2.model.Photo;

import static moe.banana.jsonapi2.TestResources.getErrorsAllFieldsSample;
import static moe.banana.jsonapi2.TestResources.getErrorsEmptySample;
import static moe.banana.jsonapi2.TestResources.getErrorsMultipleSample;
import static moe.banana.jsonapi2.TestResources.getErrorsNoFieldsSample;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ErrorTest {

    @Test
    public void simple_error() throws Exception {
        JsonApiError jsonApiError = parseErrors(getErrorsEmptySample());
        assertNotNull(jsonApiError.getErrors());
    }

    @Test
    public void empty_error_object() throws Exception {
        List<Error> errors = parseErrors(getErrorsNoFieldsSample()).getErrors();
        assertEquals(1, errors.size());
        Error emptyError = errors.get(0);
        assertNull(emptyError.getCode());
        assertNull(emptyError.getDetail());
        assertNull(emptyError.getId());
        assertNull(emptyError.getStatus());
        assertNull(emptyError.getTitle());
    }

    @Test
    public void full_error_object() throws Exception {
        List<Error> errors = parseErrors(getErrorsAllFieldsSample()).getErrors();

        assertEquals(1, errors.size());
        Error error = errors.get(0);

        assertEquals("code", error.getCode());
        assertEquals("detail", error.getDetail());
        assertEquals("id", error.getId());
        assertEquals("status", error.getStatus());
        assertEquals("title", error.getTitle());
    }

    @Test
    public void multiple_errors() throws Exception {
        List<Error> errors = parseErrors(getErrorsMultipleSample()).getErrors();
        assertEquals(2, errors.size());
        Error error1 = errors.get(0);
        Error error2 = errors.get(1);

        assertEquals("id", error1.getId());
        assertEquals("code", error1.getCode());
        assertEquals("detail", error1.getDetail());
        assertEquals("status", error1.getStatus());
        assertEquals("title", error1.getTitle());

        assertEquals("id2", error2.getId());
        assertEquals("code", error2.getCode());
        assertEquals("detail", error2.getDetail());
        assertEquals("status", error2.getStatus());
        assertEquals("title", error2.getTitle());
    }

    @Test
    public void errors_equal() throws Exception {
        assertEquals(createError(), createError());
    }

    @Test
    public void hashcodes_equal() throws Exception {
        assertEquals(createError().hashCode(), createError().hashCode());
    }

    @Test
    public void tostring_equal() throws Exception {
        assertEquals(createError().toString(), createError().toString());
    }

    private Error createError() {
        Error error = new Error();
        error.setId("id");
        error.setCode("code");
        error.setDetail("detail");
        error.setStatus("status");
        error.setTitle("title");
        return error;
    }

    private static Moshi moshi() {
        Moshi.Builder builder = new Moshi.Builder();
        builder.add(ResourceAdapterFactory.builder()
                .add(Article.class)
                .add(Person.class)
                .add(Comment.class)
                .add(Photo.class)
                .build());
        return builder.build();
    }

    private JsonApiError parseErrors(String json) throws java.io.IOException {
        return moshi().adapter(JsonApiError.class).fromJson(json);
    }

}
