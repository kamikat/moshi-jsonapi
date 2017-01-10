package moe.banana.jsonapi2;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import okio.Buffer;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MoshiTest {

    @Test
    public void equality() throws Exception {
        String json = TestUtil.fromResource("/sample.json");
        Buffer a = new Buffer();
        Buffer b = new Buffer();
        a.writeString(json, Charset.forName("UTF-8"));
        MoshiHelper.dump(JsonReader.of(a), b);
        assertEquals(b.readString(Charset.forName("UTF-8")), json);
    }

}
