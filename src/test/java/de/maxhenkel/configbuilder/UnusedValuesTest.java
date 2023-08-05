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

public class UnusedValuesTest {

    @Test
    @DisplayName("Remove unused values")
    void removeUnused(@TempDir Path tempDir) throws IOException {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Files.write(configPath, Arrays.asList("test=test123", "test1=123", "test2=456"));

        ConfigBuilderImpl builder = TestUtils.createBuilder(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "");
        builder.removeUnused();
        builder.config.saveSync();

        assertEquals("test123", entry.get());

        List<String> strings = Files.readAllLines(configPath);

        assertEquals(1, strings.size());
        assertEquals("test=test123", strings.get(0));
    }

}
