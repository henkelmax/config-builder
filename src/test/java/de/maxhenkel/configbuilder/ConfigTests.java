package de.maxhenkel.configbuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigTests {

    private static final String CONFIG_NAME = "config.properties";

    @Test
    @DisplayName("Save and read")
    void saveAndRead(@TempDir Path tempDir) {
        Path config = tempDir.resolve(CONFIG_NAME);
        ConfigBuilder builder = ConfigBuilder.buildInternal(config);
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

        builder = ConfigBuilder.buildInternal(config);
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
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
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

        sleep();
        builder.reloadFromDisk();

        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());
    }

    @Test
    @DisplayName("Save, reload and read async")
    void saveReloadAndReadAsync(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
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

        sleep();
        builder.reloadFromDisk();

        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());
    }

    @Test
    @DisplayName("Change and read without saving")
    void changeAndReadWithoutSaving(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<Boolean> booleanEntry = builder.booleanEntry("boolean_test", false);
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 10, 0, 20);
        ConfigEntry<String> stringEntry = builder.stringEntry("string_test", "Test 123");
        builder.config.save();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
        booleanEntry.set(true);
        integerEntry.set(15);
        stringEntry.set("Another string");
        assertEquals(true, booleanEntry.get());
        assertEquals(15, integerEntry.get());
        assertEquals("Another string", stringEntry.get());

        builder.reloadFromDisk();

        assertEquals(false, booleanEntry.get());
        assertEquals(10, integerEntry.get());
        assertEquals("Test 123", stringEntry.get());
    }

    @Test
    @DisplayName("Async save spamming")
    void asyncSaveSpamming(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<Integer> integerEntry = builder.integerEntry("integer_test", 0, 0, 20);
        builder.config.save();

        for (int i = 1; i <= 20; i++) {
            integerEntry.set(i).save();
        }
        assertEquals(20, integerEntry.get());

        sleep();
        builder.reloadFromDisk();

        assertEquals(20, integerEntry.get());
    }

    @Test
    @DisplayName("Set boolean")
    void setBoolean(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<Boolean> entry = builder.booleanEntry("boolean_test", false);
        builder.config.save();

        assertEquals(false, entry.get());
        entry.set(true).saveSync();
        assertEquals(true, entry.get());

        sleep();
        builder.reloadFromDisk();

        assertEquals(true, entry.get());
    }

    @Test
    @DisplayName("Set integer")
    void setInteger(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<Integer> entry = builder.integerEntry("integer_test", 10, 0, 20);
        builder.config.save();

        assertEquals(10, entry.get());
        entry.set(15).saveSync();
        assertEquals(15, entry.get());

        sleep();
        builder.reloadFromDisk();

        assertEquals(15, entry.get());

        entry.set(30).saveSync();
        assertEquals(20, entry.get());

        entry.set(30).saveSync();

        sleep();
        builder.reloadFromDisk();

        assertEquals(20, entry.get());

        entry.set(-10).saveSync();
        assertEquals(0, entry.get());
    }

    @Test
    @DisplayName("Set double")
    void setDouble(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<Double> entry = builder.doubleEntry("double_test", 10D, 0D, 20D);
        builder.config.save();

        assertEquals(10D, entry.get());
        entry.set(15D).saveSync();
        assertEquals(15D, entry.get());

        sleep();
        builder.reloadFromDisk();

        assertEquals(15D, entry.get());

        entry.set(30D).saveSync();
        assertEquals(20D, entry.get());

        entry.set(30D).saveSync();

        sleep();
        builder.reloadFromDisk();

        assertEquals(20D, entry.get());

        entry.set(-10D).saveSync();
        assertEquals(0D, entry.get());
    }

    @Test
    @DisplayName("Set string")
    void setString(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<String> entry = builder.stringEntry("string_test", "test123=!\"");
        builder.config.save();

        assertEquals("test123=!\"", entry.get());
        entry.set("abc!=\"").saveSync();
        assertEquals("abc!=\"", entry.get());

        sleep();
        builder.reloadFromDisk();

        assertEquals("abc!=\"", entry.get());
    }

    @Test
    @DisplayName("Set integer list")
    void setIntList(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<List<Integer>> entry = builder.integerListEntry("int_list_test", Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        builder.config.save();

        assertEquals(Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
        entry.set(Collections.emptyList()).saveSync();
        assertEquals(Collections.emptyList(), entry.get());
        entry.set(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE)).saveSync();

        sleep();
        builder.reloadFromDisk();

        assertEquals(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
    }

    @Test
    @DisplayName("Set enum")
    void setEnum(@TempDir Path tempDir) {
        ConfigBuilder builder = ConfigBuilder.buildInternal(tempDir.resolve(CONFIG_NAME));
        ConfigEntry<TestEnum> entry = builder.enumEntry("enum_test", TestEnum.TEST_2);
        builder.config.save();

        assertEquals(TestEnum.TEST_2, entry.get());
        entry.set(TestEnum.TEST_4).saveSync();
        assertEquals(TestEnum.TEST_4, entry.get());
        entry.set(TestEnum.TEST_3).saveSync();

        sleep();
        builder.reloadFromDisk();

        assertEquals(TestEnum.TEST_3, entry.get());
    }

    @Test
    @DisplayName("Manually create config")
    void manuallyCreateConfig(@TempDir Path tempDir) throws IOException {
        Path configPath = tempDir.resolve(CONFIG_NAME);
        Files.write(configPath, Arrays.asList("test=test123"));
        sleep();
        ConfigBuilder builder = ConfigBuilder.buildInternal(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "");
        builder.config.save();

        assertEquals("test123", entry.get());
        entry.set("abc").saveSync();

        sleep();
        builder.reloadFromDisk();

        assertEquals("abc", entry.get());
    }

    @Test
    @DisplayName("Manually read config")
    void manuallyReadConfig(@TempDir Path tempDir) throws IOException {
        Path configPath = tempDir.resolve(CONFIG_NAME);
        ConfigBuilder builder = ConfigBuilder.buildInternal(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "test123");
        builder.config.save();

        assertEquals("test123", entry.get());

        sleep();
        List<String> strings = Files.readAllLines(configPath);

        assertEquals(3, strings.size());
        assertEquals("test=test123", strings.get(2));
    }

    @Test
    @DisplayName("Delete config and save")
    void deleteConfigAndSave(@TempDir Path tempDir) throws IOException {
        Path configPath = tempDir.resolve(CONFIG_NAME);
        ConfigBuilder builder = ConfigBuilder.buildInternal(configPath);
        ConfigEntry<String> entry = builder.stringEntry("test", "test123");
        builder.config.save();

        assertEquals("test123", entry.get());

        sleep();
        Files.deleteIfExists(configPath);

        entry.set("abc").saveSync();
        sleep();

        builder = ConfigBuilder.buildInternal(configPath);
        ConfigEntry<String> entry2 = builder.stringEntry("test", "test123");
        builder.config.save();

        assertEquals("abc", entry2.get());
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    enum TestEnum {
        TEST_1, TEST_2, TEST_3, TEST_4;
    }

}
