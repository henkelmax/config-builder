package de.maxhenkel.configbuilder.builder;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
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
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<Boolean> entry = builder.booleanEntry("boolean_test", false);
        TestUtils.finalizeBuilder(builder);

        assertEquals(false, entry.get());
        entry.set(true);
        // Set again to cover the case where the value is already set to the same value
        entry.set(true).saveSync();
        assertEquals(true, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(true, entry.get());
    }

    @Test
    @DisplayName("Set integer")
    void setInteger(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<Integer> entry = builder.integerEntry("integer_test", 10, 0, 20);
        TestUtils.finalizeBuilder(builder);

        assertEquals(10, entry.get());
        entry.set(15);
        // Set again to cover the case where the value is already set to the same value
        entry.set(15).saveSync();
        assertEquals(15, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(15, entry.get());

        entry.set(30).saveSync();
        assertEquals(20, entry.get());

        entry.set(30).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(20, entry.get());

        entry.set(-10).saveSync();
        assertEquals(0, entry.get());
    }

    @Test
    @DisplayName("Set long")
    void setLong(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<Long> entry = builder.longEntry("long_test", Long.MAX_VALUE - 100L, 0L, Long.MAX_VALUE - 50L);
        TestUtils.finalizeBuilder(builder);

        assertEquals(Long.MAX_VALUE - 100L, entry.get());
        entry.set(Long.MAX_VALUE - 75L);
        // Set again to cover the case where the value is already set to the same value
        entry.set(Long.MAX_VALUE - 75L).saveSync();
        assertEquals(Long.MAX_VALUE - 75L, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(Long.MAX_VALUE - 75L, entry.get());

        entry.set(Long.MAX_VALUE).saveSync();
        assertEquals(Long.MAX_VALUE - 50L, entry.get());

        entry.set(Long.MAX_VALUE).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(Long.MAX_VALUE - 50L, entry.get());

        entry.set(Long.MIN_VALUE).saveSync();
        assertEquals(0L, entry.get());
    }

    @Test
    @DisplayName("Set float")
    void setFloat(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<Float> entry = builder.floatEntry("float_test", 10F, 0F, 20F);
        TestUtils.finalizeBuilder(builder);

        assertEquals(10F, entry.get());
        entry.set(15F);
        // Set again to cover the case where the value is already set to the same value
        entry.set(15F).saveSync();
        assertEquals(15F, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(15F, entry.get());

        entry.set(30F).saveSync();
        assertEquals(20F, entry.get());

        entry.set(30F).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(20F, entry.get());

        entry.set(-10F).saveSync();
        assertEquals(0F, entry.get());
    }

    @Test
    @DisplayName("Set double")
    void setDouble(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<Double> entry = builder.doubleEntry("double_test", 10D, 0D, 20D);
        TestUtils.finalizeBuilder(builder);

        assertEquals(10D, entry.get());
        entry.set(15D);
        // Set again to cover the case where the value is already set to the same value
        entry.set(15D).saveSync();
        assertEquals(15D, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(15D, entry.get());

        entry.set(30D).saveSync();
        assertEquals(20D, entry.get());

        entry.set(30D).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(20D, entry.get());

        entry.set(-10D).saveSync();
        assertEquals(0D, entry.get());
    }

    @Test
    @DisplayName("Set string")
    void setString(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<String> entry = builder.stringEntry("string_test", "test123=!\"");
        TestUtils.finalizeBuilder(builder);

        assertEquals("test123=!\"", entry.get());
        entry.set("abc!=\"");
        // Set again to cover the case where the value is already set to the same value
        entry.set("abc!=\"").saveSync();
        assertEquals("abc!=\"", entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals("abc!=\"", entry.get());
    }

    @Test
    @DisplayName("Set integer list")
    void setIntList(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<List<Integer>> entry = builder.integerListEntry("int_list_test", Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        TestUtils.finalizeBuilder(builder);

        assertEquals(Arrays.asList(-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
        entry.set(Collections.emptyList());
        // Set again to cover the case where the value is already set to the same value
        entry.set(Collections.emptyList()).saveSync();
        assertEquals(Collections.emptyList(), entry.get());
        entry.set(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE)).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(Arrays.asList(1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE), entry.get());
    }

    @Test
    @DisplayName("Set enum")
    void setEnum(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<TestEnum> entry = builder.enumEntry("enum_test", TestEnum.TEST_2);
        TestUtils.finalizeBuilder(builder);

        assertEquals(TestEnum.TEST_2, entry.get());
        entry.set(TestEnum.TEST_4).saveSync();
        entry.set(TestEnum.TEST_4).saveSync();
        assertEquals(TestEnum.TEST_4, entry.get());
        entry.set(TestEnum.TEST_3).saveSync();

        TestUtils.reloadBuilder(builder);

        assertEquals(TestEnum.TEST_3, entry.get());
    }

    enum TestEnum {
        TEST_1, TEST_2, TEST_3, TEST_4;
    }

}
