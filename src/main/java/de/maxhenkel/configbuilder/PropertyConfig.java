package de.maxhenkel.configbuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertyConfig implements Config {

    protected Properties properties;
    protected Path path;

    public PropertyConfig(Path path) {
        this.path = path;
        this.properties = new Properties();
        loadInternal();
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public Properties getProperties() {
        return properties;
    }

    public void load() throws IOException {
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
        }
    }

    private void loadInternal() {
        try {
            load();
        } catch (IOException e) {
            System.err.println("Failed to read " + path.getFileName().toString());
            System.err.println("Using default configuration values");
            e.printStackTrace();
        }
    }

    public void reload() {
        properties = new Properties();
        loadInternal();
    }

    public void saveSync() {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create parent directory of " + path.getFileName().toString());
            e.printStackTrace();
        }

        try (OutputStream stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.store(stream, "");
        } catch (IOException e) {
            System.err.println("Failed to save " + path.getFileName().toString());
            e.printStackTrace();
        }
    }

    public void save() {
        new Thread(() -> {
            synchronized (PropertyConfig.this) {
                saveSync();
            }
        }).start();
    }

    @Override
    public Map<String, Object> getEntries() {
        return properties.entrySet().stream().collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));
    }
}
