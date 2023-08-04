package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentedPropertyConfig implements Config {

    private static final Logger LOGGER = Logger.getLogger(CommentedPropertyConfig.class.getName());
    private static final ExecutorService SAVE_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("ConfigSaver");
        return thread;
    });

    protected CommentedProperties properties;
    @Nullable
    protected Path path;

    private CommentedPropertyConfig() {

    }

    /**
     * @param path the path to the config
     * @deprecated use {@link #builder()} instead
     */
    @Deprecated
    public CommentedPropertyConfig(@Nullable Path path) {
        if (path != null) {
            this.path = path.toAbsolutePath();
        }
        this.properties = new CommentedProperties();
        reload();
    }

    /**
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private Path path;
        private boolean strict;

        private Builder() {
            strict = true;
        }

        /**
         * Sets the path to the config
         * <br/>
         * If this value is not set, the config will be in-memory only and not saved to disk
         *
         * @param path the path
         * @return the builder
         */
        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        /**
         * A strict config is compliant to Javas {@link java.util.Properties} class
         * <br/>
         * This value is <code>true</code> by default
         *
         * @param strict whether the config should be strict
         * @return the builder
         */
        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * @return the config
         */
        public CommentedPropertyConfig build() {
            CommentedPropertyConfig config = new CommentedPropertyConfig();
            if (path != null) {
                config.path = path.toAbsolutePath();
            }
            config.properties = new CommentedProperties(strict);
            config.reload();
            return config;
        }
    }

    /**
     * @param key the config key
     * @return the string representation of the value or <code>null</code> if the entry does not exist
     */
    public String get(String key) {
        return properties.get(key);
    }

    /**
     * @param key      the config key
     * @param value    the config string value
     * @param comments the comments for the config entry
     */
    public void set(String key, String value, String... comments) {
        properties.set(key, value, comments);
    }

    /**
     * @return the commented properties
     */
    public CommentedProperties getProperties() {
        return properties;
    }

    /**
     * Loads the config from the disk if the path is set and the file exists
     *
     * @throws IOException if an IO error occurs
     */
    public void load() throws IOException {
        if (path == null) {
            return;
        }
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
        }
    }

    /**
     * Clears the config and reloads all entries from the disk
     */
    public void reload() {
        properties.clear();
        try {
            load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to reload config", e);
        }
    }

    /**
     * Saves the config to the disk synchronously
     * <br/>
     * Note that this method blocks the current thread until the config is saved
     */
    public synchronized void saveSync() {
        if (path == null) {
            return;
        }
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create parent directories of config", e);
        }

        try (OutputStream stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.save(stream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save config", e);
        }
    }

    /**
     * Saves the config to the disk asynchronously
     * <br/>
     * Note that reloading the config immediately after saving it asynchronously may cause issues
     */
    public void save() {
        if (path == null) {
            return;
        }
        SAVE_EXECUTOR_SERVICE.execute(this::saveSync);
    }

    @Override
    public Map<String, String> getEntries() {
        return Collections.unmodifiableMap(properties);
    }

}
