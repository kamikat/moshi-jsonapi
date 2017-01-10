package moe.banana.jsonapi2;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import moe.banana.jsonapi2.model.Color;
import okio.Buffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtil {

    @JsonApi(type = "default")
    public static class Default extends Resource {

    }

    private static class ColorAdapter {
        @ToJson String toJson(@Color int rgb) {
            return String.format("#%06x", rgb);
        }

        @FromJson @Color int fromJson(String rgb) {
            return Integer.parseInt(rgb.substring(1), 16);
        }
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
        return new Moshi.Builder().add(factoryBuilder.build()).add(new ColorAdapter()).build();
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
