package de.maxhenkel.configbuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataTypesTest {

    @Test
    @DisplayName("Set boolean")
    void setBoolean(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Boolean> entry = builder.booleanEntry("boolean_test", false);
        builder.config.save();

        assertEquals(false, entry.get());
        entry.set(true).saveSync();
        assertEquals(true, entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(true, entry.get());
    }

    @Test
    @DisplayName("Set integer")
    void setInteger(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Integer> entry = builder.integerEntry("integer_test", 10, 0, 20);
        builder.config.save();

        assertEquals(10, entry.get());
        entry.set(15).saveSync();
        assertEquals(15, entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(15, entry.get());

        entry.set(30).saveSync();
        assertEquals(20, entry.get());

        entry.set(30).saveSync();

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(20, entry.get());

        entry.set(-10).saveSync();
        assertEquals(0, entry.get());
    }

    @Test
    @DisplayName("Set double")
    void setDouble(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<Double> entry = builder.doubleEntry("double_test", 10D, 0D, 20D);
        builder.config.save();

        assertEquals(10D, entry.get());
        entry.set(15D).saveSync();
        assertEquals(15D, entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(15D, entry.get());

        entry.set(30D).saveSync();
        assertEquals(20D, entry.get());

        entry.set(30D).saveSync();

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(20D, entry.get());

        entry.set(-10D).saveSync();
        assertEquals(0D, entry.get());
    }

    @Test
    @DisplayName("Set string")
    void setString(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<String> entry = builder.stringEntry("string_test", "test123=!\"");
        builder.config.save();

        assertEquals("test123=!\"", entry.get());
        entry.set("abc!=\"").saveSync();
        assertEquals("abc!=\"", entry.get());

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals("abc!=\"", entry.get());
    }

    @Test
    @DisplayName("Set integer list")
    void setIntList(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<List<Integer>> entry = builder.integerListEntry("int_list_test", Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        builder.config.save();

        assertEquals(Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
        entry.set(Collections.emptyList()).saveSync();
        assertEquals(Collections.emptyList(), entry.get());
        entry.set(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE)).saveSync();

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
    }

    @Test
    @DisplayName("Set enum")
    void setEnum(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(tempDir.resolve(TestUtils.CONFIG_NAME));
        ConfigEntry<TestEnum> entry = builder.enumEntry("enum_test", TestEnum.TEST_2);
        builder.config.save();

        assertEquals(TestEnum.TEST_2, entry.get());
        entry.set(TestEnum.TEST_4).saveSync();
        assertEquals(TestEnum.TEST_4, entry.get());
        entry.set(TestEnum.TEST_3).saveSync();

        TestUtils.sleep();
        builder.reloadFromDisk();

        assertEquals(TestEnum.TEST_3, entry.get());
    }

    enum TestEnum {
        TEST_1, TEST_2, TEST_3, TEST_4;
    }

}
