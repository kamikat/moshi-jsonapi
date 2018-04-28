package moe.banana.jsonapi2;

import java.lang.reflect.Field;

public interface JsonNameMapping {
    String getJsonName(Field field);
}
