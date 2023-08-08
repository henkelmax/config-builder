package de.maxhenkel.configbuilder.custom;

import java.util.Collections;
import java.util.Map;

/**
 * An unmodifiable string map that can be used as a config entry.
 */
public class StringMap extends AbstractValueMap<String, String> {

    protected StringMap(Map<String, String> map) {
        super(map);
    }

    public static StringMap of(Map<String, String> map) {
        return new StringMap(map);
    }

    public static StringMap of() {
        return new StringMap(Collections.emptyMap());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractValueMap.Builder<String, String, StringMap> {
        @Override
        public StringMap build() {
            return new StringMap(map);
        }
    }

}
