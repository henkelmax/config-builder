package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;

public interface MigratableConfig extends Config {

    @Nullable
    String get(String key);

    boolean has(String key);

    void set(String key, String value);

}
