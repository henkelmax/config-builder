package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.util.Map;

class MigratableConfigImpl implements MigratableConfig {

    private final CommentedPropertyConfig config;
    private boolean frozen;

    public MigratableConfigImpl(CommentedPropertyConfig config) {
        this.config = config;
    }

    @Nullable
    @Override
    public String get(String key) {
        checkFrozen();
        return config.get(key);
    }

    @Override
    public boolean has(String key) {
        checkFrozen();
        return config.has(key);
    }

    @Override
    public void set(String key, String value) {
        checkFrozen();
        config.set(key, value);
    }

    @Override
    public Map<String, String> getEntries() {
        checkFrozen();
        return config.getEntries();
    }

    public void freeze() {
        this.frozen = true;
    }

    private void checkFrozen() {
        if (frozen) {
            throw new IllegalStateException("ConfigBuilder is frozen");
        }
    }

}
