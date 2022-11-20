package de.maxhenkel.configbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentedPropertyConfig implements Config {

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
        properties.set(key, value);
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
            System.err.println("Failed to read " + path.getFileName().toString());
            System.err.println("Using default configuration values");
            e.printStackTrace();
        }
    }

    public void saveSync() {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create parent directory of " + path.getFileName().toString());
            e.printStackTrace();
        }

        try (OutputStream stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.save(stream);
        } catch (IOException e) {
            System.err.println("Failed to save " + path.getFileName().toString());
            e.printStackTrace();
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
    public Map<String, Object> getEntries() {
        return properties.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
