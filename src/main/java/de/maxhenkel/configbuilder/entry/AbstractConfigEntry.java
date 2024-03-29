package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.Config;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractConfigEntry<T> implements ConfigEntry<T> {

    protected final CommentedPropertyConfig config;
    protected final ValueSerializer<T> serializer;
    protected String[] comments;
    protected String key;
    protected T def;
    @Nullable
    protected T value;

    public AbstractConfigEntry(CommentedPropertyConfig config, ValueSerializer<T> serializer, String[] comments, String key, T def) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(serializer);
        Objects.requireNonNull(comments);
        Objects.requireNonNull(key);
        Objects.requireNonNull(def);
        this.config = config;
        this.serializer = serializer;
        this.comments = comments;
        this.key = key;
        this.def = def;
    }

    /**
     * Loads the config entry from the config or sets the default value if it doesn't exist.
     */
    public void reload() {
        if (config.getProperties().containsKey(key)) {
            T val = serializer.deserialize(config.getProperties().get(key));
            if (val == null) {
                reset();
            } else {
                value = fixValue(val);
                syncEntryToProperties();
            }
        } else {
            reset();
        }
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public AbstractConfigEntry<T> set(T value) {
        if (this.value != null && this.value.equals(value)) {
            return this;
        }
        this.value = fixValue(value);
        syncEntryToProperties();
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public AbstractConfigEntry<T> comment(String... comments) {
        this.comments = comments;
        syncEntryToProperties();
        return this;
    }

    @Override
    public String[] getComments() {
        return comments;
    }

    @Override
    public ConfigEntry<T> reset() {
        value = def;
        syncEntryToProperties();
        return this;
    }

    private void syncEntryToProperties() {
        String serialized = serializer.serialize(value);
        if (serialized == null) {
            if (value == def) {
                throw new IllegalStateException("Failed to serialize default value");
            }
            reset();
            return;
        }
        config.getProperties().set(key, serialized, comments);
    }

    @Override
    public ConfigEntry<T> save() {
        config.save();
        return this;
    }

    @Override
    public ConfigEntry<T> saveSync() {
        config.saveSync();
        return this;
    }

    /**
     * Fixes the value if it is invalid or out of bounds.
     *
     * @param value the value to fix
     * @return the fixed value
     */
    T fixValue(T value) {
        return value;
    }

    @Override
    public T getDefault() {
        return def;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    public ValueSerializer<T> getSerializer() {
        return serializer;
    }
}
