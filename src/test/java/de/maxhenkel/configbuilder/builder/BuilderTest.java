package de.maxhenkel.configbuilder.builder;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertEquals(false, cfg.booleanEntry.get());
        Assertions.assertEquals(10, cfg.integerEntry.get());
        Assertions.assertEquals(10L, cfg.longEntry.get());
        Assertions.assertEquals(10F, cfg.floatEntry.get());
        Assertions.assertEquals(10D, cfg.doubleEntry.get());
        Assertions.assertEquals("test123", cfg.stringEntry.get());
        Assertions.assertEquals(TestEnum.TEST_1, cfg.enumEntry.get());

        Map<String, String> entries = cfg.stringEntry.getConfig().getEntries();

        assertEquals(7, entries.size());
        assertEquals("false", entries.get("boolean"));
        assertEquals("10", entries.get("integer"));
        assertEquals("10", entries.get("long"));
        assertEquals("10.0", entries.get("float"));
        assertEquals("10.0", entries.get("double"));
        assertEquals("test123", entries.get("string"));
        assertEquals("TEST_1", entries.get("enum"));

        Assertions.assertEquals(false, cfg.booleanEntry.getDefault());
        Assertions.assertEquals(10, cfg.integerEntry.getDefault());
        Assertions.assertEquals(10L, cfg.longEntry.getDefault());
        Assertions.assertEquals(10F, cfg.floatEntry.getDefault());
        Assertions.assertEquals(10D, cfg.doubleEntry.getDefault());
        Assertions.assertEquals("test123", cfg.stringEntry.getDefault());
        Assertions.assertEquals(TestEnum.TEST_1, cfg.enumEntry.getDefault());

        Assertions.assertEquals(1, cfg.booleanEntry.getComments().length);
        Assertions.assertEquals("Test boolean", cfg.booleanEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.integerEntry.getComments().length);
        Assertions.assertEquals("Test integer", cfg.integerEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.longEntry.getComments().length);
        Assertions.assertEquals("Test long", cfg.longEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.floatEntry.getComments().length);
        Assertions.assertEquals("Test float", cfg.floatEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.doubleEntry.getComments().length);
        Assertions.assertEquals("Test double", cfg.doubleEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.stringEntry.getComments().length);
        Assertions.assertEquals("Test string", cfg.stringEntry.getComments()[0]);
        Assertions.assertEquals(1, cfg.enumEntry.getComments().length);
        Assertions.assertEquals("Test enum", cfg.enumEntry.getComments()[0]);
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
