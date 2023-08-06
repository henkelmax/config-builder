package de.maxhenkel.configbuilder;

import java.nio.file.Path;
import java.util.UUID;

public class TestUtils {

    public static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path randomConfigName(Path tempDir) {
        return tempDir.resolve(String.format("config_%s.properties", UUID.randomUUID()));
    }

    public static ConfigBuilderImpl createBuilderWithRandomPath(Path tempDir) {
        return createBuilder(randomConfigName(tempDir));
    }

    public static ConfigBuilderImpl createBuilder(Path configPath) {
        return new ConfigBuilderImpl(CommentedPropertyConfig.builder().path(configPath).build());
    }

    public static ConfigBuilderImpl createInMemoryBuilder() {
        return createBuilder(null);
    }

    public static void finalizeBuilder(ConfigBuilderImpl builder) {
        builder.config.saveSync();
    }

    public static void reloadBuilder(ConfigBuilderImpl builder) {
        builder.reloadFromDisk();
    }

    public static CommentedProperties getProperties(ConfigBuilderImpl builder) {
        return builder.config.getProperties();
    }

    public static void removeUnused(ConfigBuilderImpl builder) {
        builder.removeUnused();
    }

}
