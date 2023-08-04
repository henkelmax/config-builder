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

}
