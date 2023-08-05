package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HeaderTest {

    @Test
    @DisplayName("Set header")
    void setString(@TempDir Path tempDir) throws IOException {
        Path configPath = TestUtils.randomConfigName(tempDir);
        ConfigBuilderImpl builder = TestUtils.createBuilder(configPath);
        builder.header("Header 1", "Header 2", "Header 3\nHeader 4");
        ConfigEntry<String> entry = builder.stringEntry("test", "123");
        builder.config.saveSync();

        List<String> strings = Files.readAllLines(configPath);

        assertEquals(6, strings.size());
        assertEquals("# Header 1", strings.get(0));
        assertEquals("# Header 2", strings.get(1));
        assertEquals("# Header 3", strings.get(2));
        assertEquals("# Header 4", strings.get(3));
        assertEquals("", strings.get(4));
        assertEquals("test=123", strings.get(5));
        assertEquals("test", entry.getKey());
        assertEquals("123", entry.get());
    }

}
