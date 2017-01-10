package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Meta;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class JsonBufferTest {

    private Meta createMeta() {
        Meta meta = new Meta();
        meta.copyright = "Copyright 20XX Example Corp.";
        meta.authors = Arrays.asList(
                "Yehuda Katz",
                "Steve Klabnik",
                "Dan Gebhardt",
                "Tyler Kellen");
        return meta;
    }

    private JsonBuffer<Meta> createJsonBuffer() {
        return JsonBuffer.create(TestUtil.moshi().adapter(Meta.class), createMeta());
    }

    @Test
    public void equality() throws Exception {
        JsonBuffer<Meta> buffer1 = createJsonBuffer();
        JsonBuffer<Meta> buffer2 = createJsonBuffer();
        assertEquals(buffer1, buffer2);
        assertNotEquals(buffer1, JsonBuffer.create(TestUtil.moshi().adapter(Meta.class), null));
        assertEquals(buffer1.hashCode(), buffer2.hashCode());
    }

    @Test
    public void serialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(JsonBuffer.class).toJson(createJsonBuffer()),
                equalTo("{\"authors\":[\"Yehuda Katz\",\"Steve Klabnik\",\"Dan Gebhardt\",\"Tyler Kellen\"],\"copyright\":\"Copyright 20XX Example Corp.\"}"));
    }

    @Test
    public void deserialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(JsonBuffer.class).fromJson("{\"authors\":[\"Yehuda Katz\",\"Steve Klabnik\",\"Dan Gebhardt\",\"Tyler Kellen\"],\"copyright\":\"Copyright 20XX Example Corp.\"}"),
                equalTo((JsonBuffer) createJsonBuffer()));
    }

    @Test
    public void resolve() {
        assertThat(createJsonBuffer().get(TestUtil.moshi().adapter(Meta.class)),
                equalTo(createMeta()));
    }

}
