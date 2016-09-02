package moe.banana.jsonapi2;

import java.util.Scanner;

public final class TestResources {

    public static String readResource(String resourceName) {
        Scanner scanner = new Scanner(TestResources.class.getResourceAsStream(resourceName), "UTF-8");
        return scanner.useDelimiter("\\A").next();
    }

    public static String getPrivateSample() {
        return readResource("/private.json");
    }

    public static String getPrivateSampleSerialized() {
        return readResource("/private_serialized.json").trim();
    }

}
