package moe.banana.jsonapi2;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ErrorTest {

    private Error createError() {
        Error error = new Error();
        error.setId("1");
        error.setStatus("409");
        error.setCode("E409");
        error.setTitle("Conflict");
        error.setDetail("Detail error description");
        return error;
    }

    @Test
    public void equality() throws Exception {
        assertEquals(createError(), createError());
    }

    @Test
    public void hashcode() throws Exception {
        assertEquals(createError().hashCode(), createError().hashCode());
    }

    @Test
    public void serialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(Error.class).toJson(createError()),
                equalTo("{\"id\":\"1\",\"status\":\"409\",\"code\":\"E409\",\"title\":\"Conflict\",\"detail\":\"Detail error description\"}"));
    }

    @Test
    public void deserialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(Error.class).fromJson("{\"id\":\"1\",\"status\":\"409\",\"code\":\"E409\",\"title\":\"Conflict\",\"detail\":\"Detail error description\"}"),
                equalTo(createError()));
    }

}
