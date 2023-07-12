package de.maxhenkel.configbuilder;

import java.nio.file.Path;
import java.util.UUID;

public class TestUtils {

    public static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Path randomConfigName(Path tempDir) {
        return tempDir.resolve(String.format("config_%s.properties", UUID.randomUUID()));
    }

}
