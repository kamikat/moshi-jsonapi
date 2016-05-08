package moe.banana.jsonapi;

import java.util.HashMap;
import java.util.Map;

public final class Maps {

    public static class Builder<T> {
        private Map<String, T> mMap = new HashMap<>();
        private String mKey;

        public Builder key(String key) {
            if (mKey != null) {
                throw new AssertionError("duplicated key call");
            }
            mKey = key;
            return this;
        }

        public Builder val(T value) {
            mMap.put(mKey, value);
            mKey = null;
            return this;
        }

        public Map<String, T> build() {
            return new HashMap<>(mMap);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

}
