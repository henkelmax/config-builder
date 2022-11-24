package de.maxhenkel.configbuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveTest {

    @Test
    @DisplayName("Save and read")
    void saveAndRead(@TempDir Path tempDir) {
        Path config = tempDir.resolve(TestUtils.CONFIG_NAME);
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(config);
        ConfigEntry<Boolean> booleanEntry = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry = builder.stringEntry("string_test", "Test 123");
        builder.config.save();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
        booleanEntry.set(true).saveSync();
        integerEntry.set(15).saveSync();
        stringEntry.set("Another string").saveSync();
        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());

        builder = ConfigBuilderImpl.buildInternal(config);
        ConfigEntry<Boolean> booleanEntry2 = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry2 = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry2 = builder.stringEntry("string_test", "Test 123");
        builder.config.save();

        assertEquals(true, booleanEntry2.get());
        assertEquals(15, integerEntry2.get());
        assertEquals("Another string", stringEntry2.get());
    }

    @Test
    @DisplayName("Save, reload and read")
    void saveReloadAndRead(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Boolean> booleanEntry = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry = builder.stringEntry("string_test", "Test 123");
        builder.config.save();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
        booleanEntry.set(true).saveSync();
        integerEntry.set(15).saveSync();
        stringEntry.set("Another string").saveSync();
        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());
    }

    @Test
    @DisplayName("Save, reload and read async")
    void saveReloadAndReadAsync(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Boolean> booleanEntry = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry = builder.stringEntry("string_test", "Test 123");
        builder.config.save();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
        booleanEntry.set(true).save();
        integerEntry.set(15).save();
        stringEntry.set("Another string").save();
        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());
    }

    @Test
    @DisplayName("Change and read without saving")
    void changeAndReadWithoutSaving(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Boolean> booleanEntry = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry = builder.stringEntry("string_test", "Test 123");
        builder.config.save();
        TestUtils.sleep();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
        booleanEntry.set(true);
        integerEntry.set(15);
        stringEntry.set("Another string");
        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
    }

    @Test
    @DisplayName("Async save spamming")
    void asyncSaveSpamming(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 0, 0, 20);
        builder.config.save();

        for (int i = 1; i <= 20; i++) {
            integerEntry.set(i).save();
        }
        assertEquals(20, integerEntry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(20, integerEntry.get());
    }

    @Test
    @DisplayName("Delete config and save")
    void deleteConfigAndSave(@TempDir Path tempDir) throws IOException {
        Path configPath = tempDir.resolve(TestUtils.CONFIG_NAME);
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "test123");
        builder.config.save();

        assertEquals("test123", entry.get());

        TestUtils.sleep();
        Files.deleteIfExists(configPath);

        entry.set("abc").saveSync();
        TestUtils.sleep();

        builder = ConfigBuilderImpl.buildInternal(configPath);
        ConfigEntry<String> entry2 = builder.stringEntry("test", "test123");
        builder.config.save();

        assertEquals("abc", entry2.get());
    }

    @Test
    @DisplayName("Reset and read again")
    void resetAndRead(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<String> entry = builder.stringEntry("test", "123");
        builder.config.save();

        assertEquals("123", entry.get());
        entry.set("456").saveSync();
        assertEquals("456", entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals("456", entry.get());
        entry.reset().saveSync();

        assertEquals("123", entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals("123", entry.get());
    }

}
