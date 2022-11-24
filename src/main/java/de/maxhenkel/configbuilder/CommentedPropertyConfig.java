package de.maxhenkel.configbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommentedPropertyConfig implements Config {

    private static final Logger LOGGER = Logger.getLogger(CommentedPropertyConfig.class.getName());

    protected CommentedProperties properties;
    protected Path path;

    public CommentedPropertyConfig(Path path) {
        this.path = path;
        this.properties = new CommentedProperties();
        reload();
    }

    public String get(String key) {
        return properties.get(key);
    }

    public void set(String key, String value, String... comments) {
        properties.set(key, value, comments);
    }

    public CommentedProperties getProperties() {
        return properties;
    }

    public void load() throws IOException {
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
        }
    }

    public void reload() {
        properties.clear();
        try {
            load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to reload config", e);
        }
    }

    public void saveSync() {
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

    public void save() {
        new Thread(() -> {
            synchronized (CommentedPropertyConfig.this) {
                saveSync();
            }
        }).start();
    }

    @Override
    public Map<String, String> getEntries() {
        return properties.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
