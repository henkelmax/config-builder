package de.maxhenkel.configbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    protected Properties properties;
    protected Path path;

    public Config(Path path) {
        this.path = path;
        this.properties = new Properties();
        try {
            load();
        } catch (IOException e) {
            System.err.println("Failed to read " + path.getFileName().toString());
            System.err.println("Using default configuration values");
            e.printStackTrace();
        }
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
        File file = path.toFile();
        if (file.exists()) {
            properties.load(new FileInputStream(file));
        }
    }

    private void saveUnthreaded() {
        try {
            File file = path.toFile();
            file.getParentFile().mkdirs();
            properties.store(new FileWriter(file, false), "");
        } catch (IOException e) {
            System.err.println("Failed to save " + path.getFileName().toString());
            e.printStackTrace();
        }
    }

    public void save() {
        new Thread(() -> {
            synchronized (Config.this) {
                saveUnthreaded();
            }
        }).start();
    }

}
