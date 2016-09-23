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

    public static String getErrorsEmptySample() {
        return readResource("/errors_empty.json");
    }

    public static String getErrorsNoFieldsSample() {
        return readResource("/errors_no_fields.json");
    }

    public static String getErrorsAllFieldsSample() {
        return readResource("/errors_all_fields.json");
    }

    public static String getErrorsMultipleSample() {
        return readResource("/errors_multiple.json");
    }

}
