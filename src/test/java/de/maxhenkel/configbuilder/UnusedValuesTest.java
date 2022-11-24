package de.maxhenkel.configbuilder;

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
        Path configPath = tempDir.resolve(TestUtils.CONFIG_NAME);
        Files.write(configPath, Arrays.asList("test=test123", "test1=123", "test2=456"));
        TestUtils.sleep();
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "");
        builder.removeUnused();
        builder.config.save();

        assertEquals("test123", entry.get());

        TestUtils.sleep();

        List<String> strings = Files.readAllLines(configPath);

        assertEquals(1, strings.size());
        assertEquals("test=test123", strings.get(0));
    }

}
