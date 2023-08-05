package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManualConfigTest {

    @Test
    @DisplayName("Manually create config")
    void manuallyCreateConfig(@TempDir Path tempDir) throws IOException {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Files.write(configPath, Arrays.asList("test=test123"));

        ConfigBuilderImpl builder = TestUtils.createBuilder(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "");
        builder.config.saveSync();

        assertEquals("test123", entry.get());
        entry.set("abc").saveSync();

        builder.reloadFromDisk();

        assertEquals("abc", entry.get());
    }

    @Test
    @DisplayName("Manually read config")
    void manuallyReadConfig(@TempDir Path tempDir) throws IOException {
        Path configPath = TestUtils.randomConfigName(tempDir);
        ConfigBuilderImpl builder = TestUtils.createBuilder(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "test123");
        builder.config.saveSync();

        assertEquals("test123", entry.get());

        List<String> strings = Files.readAllLines(configPath);

        assertEquals(1, strings.size());
        assertEquals("test=test123", strings.get(0));
    }

}
