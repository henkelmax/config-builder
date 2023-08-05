package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderTest {

    @Test
    @DisplayName("Builder with saving")
    void builder(@TempDir Path tempDir) {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Config cfg = ConfigBuilder.builder(Config::new).path(configPath).keepOrder(true).removeUnused(true).strict(true).build();

        cfg.enumEntry.saveSync();
        cfg.stringEntry.save();

        // Wait a while until the async save is done
        TestUtils.sleep();
    }

    @Test
    @DisplayName("Builder without saving")
    void builder() {
        Config cfg = ConfigBuilder.builder(Config::new).keepOrder(true).removeUnused(true).strict(true).saveAfterBuild(false).build();

        assertEquals(false, cfg.booleanEntry.get());
        assertEquals(10, cfg.integerEntry.get());
        assertEquals(10L, cfg.longEntry.get());
        assertEquals(10D, cfg.doubleEntry.get());
        assertEquals("test123", cfg.stringEntry.get());
        assertEquals(TestEnum.TEST_1, cfg.enumEntry.get());

        Map<String, String> entries = cfg.stringEntry.getConfig().getEntries();

        assertEquals(6, entries.size());
        assertEquals("false", entries.get("boolean"));
        assertEquals("10", entries.get("integer"));
        assertEquals("10", entries.get("long"));
        assertEquals("10.0", entries.get("double"));
        assertEquals("test123", entries.get("string"));
        assertEquals("TEST_1", entries.get("enum"));
    }

    @Test
    @DisplayName("Save builder without path")
    void saveWithoutPath() {
        Config cfg = ConfigBuilder.builder(Config::new).keepOrder(true).removeUnused(true).strict(true).saveAfterBuild(true).build();
        cfg.stringEntry.saveSync();
        cfg.enumEntry.save();
    }

    private static class Config {
        public final ConfigEntry<Boolean> booleanEntry;
        public final ConfigEntry<Integer> integerEntry;
        public final ConfigEntry<Long> longEntry;
        public final ConfigEntry<Double> doubleEntry;
        public final ConfigEntry<String> stringEntry;
        public final ConfigEntry<TestEnum> enumEntry;

        public Config(ConfigBuilder builder) {
            booleanEntry = builder.booleanEntry("boolean", false);
            integerEntry = builder.integerEntry("integer", 10, 0, 20);
            longEntry = builder.longEntry("long", 10L, 0L, 20L);
            doubleEntry = builder.doubleEntry("double", 10D, 0D, 20D);
            stringEntry = builder.stringEntry("string", "test123");
            enumEntry = builder.enumEntry("enum", TestEnum.TEST_1);
        }
    }

    public enum TestEnum {
        TEST_1, TEST_2, TEST_3;
    }

}
