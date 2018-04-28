package moe.banana.jsonapi2;

import com.squareup.moshi.Json;

import java.lang.reflect.Field;

public class MoshiJsonNameMapping implements JsonNameMapping {
    @Override
    public String getJsonName(Field field) {
        String name = field.getName();
        Json json = field.getAnnotation(Json.class);
        if (json != null) {
            name = json.name();
        }
        return name;
    }
}
