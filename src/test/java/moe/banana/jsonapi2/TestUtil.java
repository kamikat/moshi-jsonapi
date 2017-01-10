package moe.banana.jsonapi2;

import com.squareup.moshi.Moshi;
import okio.Buffer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtil {

    @JsonApi(type = "default")
    public static class Default extends Resource {

    }

    @SafeVarargs
    public static Moshi moshi(Class<? extends Resource>... types) {
        return moshi(false, types);
    }

    @SafeVarargs
    public static Moshi moshi(boolean strict, Class<? extends Resource>... types) {
        ResourceAdapterFactory.Builder factoryBuilder = ResourceAdapterFactory.builder();
        factoryBuilder.add(types);
        if (!strict) {
            factoryBuilder.add(Default.class);
        }
        return new Moshi.Builder().add(factoryBuilder.build()).build();
    }

    public static String fromResource(String resourceName) throws IOException {
        InputStream in = TestUtil.class.getResourceAsStream(resourceName);
        if (in == null) {
            throw new FileNotFoundException(resourceName);
        }
        Buffer buffer = new Buffer();
        buffer.readFrom(in);
        return buffer.readString(Charset.forName("UTF-8"));
    }

}
