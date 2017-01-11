package moe.banana.jsonapi2;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import okio.Buffer;
import org.junit.Test;

import java.io.EOFException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MoshiHelperTest {

    @Test
    public void equality() throws Exception {
        String json = TestUtil.fromResource("/sample.json");
        Buffer a = new Buffer();
        Buffer b = new Buffer();
        a.writeString(json, Charset.forName("UTF-8"));
        MoshiHelper.dump(JsonReader.of(a), b);
        assertEquals(b.readString(Charset.forName("UTF-8")), json);
    }

    @Test(expected = EOFException.class)
    public void eof_exception() throws Exception {
        Buffer a = new Buffer();
        Buffer b = new Buffer();
        MoshiHelper.dump(JsonReader.of(a), b);
    }

}
