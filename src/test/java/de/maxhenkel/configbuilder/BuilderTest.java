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
    @DisplayName("Old builder")
    void oldBuilder(@TempDir Path tempDir) {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Config cfg = ConfigBuilder.build(configPath, Config::new);
        checkConfig(cfg);
    }

    @Test
    @DisplayName("Old builder remove unused")
    void oldBuilderRemoveUnused(@TempDir Path tempDir) {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Config cfg = ConfigBuilder.build(configPath, true, Config::new);
        checkConfig(cfg);
    }

    @Test
    @DisplayName("Builder with saving")
    void builder(@TempDir Path tempDir) {
        Path configPath = TestUtils.randomConfigName(tempDir);
        Config cfg = ConfigBuilder.builder(Config::new).path(configPath).keepOrder(true).removeUnused(true).strict(true).build();

        cfg.enumEntry.saveSync();
        cfg.stringEntry.save();

        checkConfig(cfg);

        // Wait a while until the async save is done
        TestUtils.sleep();
    }

    @Test
    @DisplayName("Builder without saving")
    void builder() {
        Config cfg = ConfigBuilder.builder(Config::new).keepOrder(true).removeUnused(true).strict(true).saveAfterBuild(false).build();
        checkConfig(cfg);
    }

    @Test
    @DisplayName("Save builder without path")
    void saveWithoutPath() {
        Config cfg = ConfigBuilder.builder(Config::new).keepOrder(true).removeUnused(true).strict(true).saveAfterBuild(true).build();
        cfg.stringEntry.saveSync();
        cfg.enumEntry.save();
    }

    private void checkConfig(Config cfg) {
        assertEquals(false, cfg.booleanEntry.get());
        assertEquals(10, cfg.integerEntry.get());
        assertEquals(10L, cfg.longEntry.get());
        assertEquals(10F, cfg.floatEntry.get());
        assertEquals(10D, cfg.doubleEntry.get());
        assertEquals("test123", cfg.stringEntry.get());
        assertEquals(TestEnum.TEST_1, cfg.enumEntry.get());

        Map<String, String> entries = cfg.stringEntry.getConfig().getEntries();

        assertEquals(7, entries.size());
        assertEquals("false", entries.get("boolean"));
        assertEquals("10", entries.get("integer"));
        assertEquals("10", entries.get("long"));
        assertEquals("10.0", entries.get("float"));
        assertEquals("10.0", entries.get("double"));
        assertEquals("test123", entries.get("string"));
        assertEquals("TEST_1", entries.get("enum"));

        assertEquals(false, cfg.booleanEntry.getDefault());
        assertEquals(10, cfg.integerEntry.getDefault());
        assertEquals(10L, cfg.longEntry.getDefault());
        assertEquals(10F, cfg.floatEntry.getDefault());
        assertEquals(10D, cfg.doubleEntry.getDefault());
        assertEquals("test123", cfg.stringEntry.getDefault());
        assertEquals(TestEnum.TEST_1, cfg.enumEntry.getDefault());

        assertEquals(1, cfg.booleanEntry.getComments().length);
        assertEquals("Test boolean", cfg.booleanEntry.getComments()[0]);
        assertEquals(1, cfg.integerEntry.getComments().length);
        assertEquals("Test integer", cfg.integerEntry.getComments()[0]);
        assertEquals(1, cfg.longEntry.getComments().length);
        assertEquals("Test long", cfg.longEntry.getComments()[0]);
        assertEquals(1, cfg.floatEntry.getComments().length);
        assertEquals("Test float", cfg.floatEntry.getComments()[0]);
        assertEquals(1, cfg.doubleEntry.getComments().length);
        assertEquals("Test double", cfg.doubleEntry.getComments()[0]);
        assertEquals(1, cfg.stringEntry.getComments().length);
        assertEquals("Test string", cfg.stringEntry.getComments()[0]);
        assertEquals(1, cfg.enumEntry.getComments().length);
        assertEquals("Test enum", cfg.enumEntry.getComments()[0]);
    }

    private static class Config {
        public final ConfigEntry<Boolean> booleanEntry;
        public final ConfigEntry<Integer> integerEntry;
        public final ConfigEntry<Long> longEntry;
        public final ConfigEntry<Float> floatEntry;
        public final ConfigEntry<Double> doubleEntry;
        public final ConfigEntry<String> stringEntry;
        public final ConfigEntry<TestEnum> enumEntry;

        public Config(ConfigBuilder builder) {
            booleanEntry = builder.booleanEntry("boolean", false).comment("Test boolean");
            integerEntry = builder.integerEntry("integer", 10, 0, 20).comment("Test integer");
            longEntry = builder.longEntry("long", 10L, 0L, 20L).comment("Test long");
            floatEntry = builder.floatEntry("float", 10F, 0F, 20F).comment("Test float");
            doubleEntry = builder.doubleEntry("double", 10D, 0D, 20D).comment("Test double");
            stringEntry = builder.stringEntry("string", "test123").comment("Test string");
            enumEntry = builder.enumEntry("enum", TestEnum.TEST_1).comment("Test enum");
        }
    }

    public enum TestEnum {
        TEST_1, TEST_2, TEST_3;
    }

}
