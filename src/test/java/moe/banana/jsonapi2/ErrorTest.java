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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ErrorTest {

    @Test(expected = JsonApiErrorException.class)
    public void when_errorsAvailable_thenThrow() throws Exception {
        moshi().adapter(Article.class).fromJson(getErrorsEmptySample());
    }

    @Test
    public void empty_error_array() throws Exception {
        try {
            moshi().adapter(Article.class).fromJson(getErrorsEmptySample());
        } catch (JsonApiErrorException e) {
            assertTrue(e.getErrors().isEmpty());
        }
    }

    @Test
    public void empty_error_object() throws Exception {
        try {
            moshi().adapter(Article.class).fromJson(getErrorsNoFieldsSample());
        } catch (JsonApiErrorException e) {
            List<Error> errors = e.getErrors();
            assertEquals(1, errors.size());
            Error emptyError = errors.get(0);
            assertNull(emptyError.getCode());
            assertNull(emptyError.getDetail());
            assertNull(emptyError.getId());
            assertNull(emptyError.getStatus());
            assertNull(emptyError.getTitle());
        }
    }

    @Test
    public void full_error_object() throws Exception {
        try {
            moshi().adapter(Article.class).fromJson(getErrorsAllFieldsSample());
        } catch (JsonApiErrorException e) {
            List<Error> errors = e.getErrors();
            assertEquals(1, errors.size());
            Error error = errors.get(0);

            assertEquals("code", error.getCode());
            assertEquals("detail", error.getDetail());
            assertEquals("id", error.getId());
            assertEquals("status", error.getStatus());
            assertEquals("title", error.getTitle());
        }
    }

    @Test
    public void multiple_errors() throws Exception {
        try {
            moshi().adapter(Article.class).fromJson(getErrorsMultipleSample());
        } catch (JsonApiErrorException e) {
            List<Error> errors = e.getErrors();
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

}
