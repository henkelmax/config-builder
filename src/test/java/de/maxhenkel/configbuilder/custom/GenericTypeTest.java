package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericTypeTest {

    @Test
    @DisplayName("Generic boolean")
    void genericBoolean(@TempDir Path tempDir) {
        testGenericValue(tempDir, false, true);
    }

    @Test
    @DisplayName("Generic integer")
    void genericInteger(@TempDir Path tempDir) {
        testGenericValue(tempDir, 10, 15);
    }

    @Test
    @DisplayName("Generic long")
    void genericLong(@TempDir Path tempDir) {
        testGenericValue(tempDir, 10L, 15L);
    }

    @Test
    @DisplayName("Generic float")
    void genericFloat(@TempDir Path tempDir) {
        testGenericValue(tempDir, 10F, 15F);
    }

    @Test
    @DisplayName("Generic double")
    void genericDouble(@TempDir Path tempDir) {
        testGenericValue(tempDir, 10D, 15D);
    }

    @Test
    @DisplayName("Generic string")
    void genericString(@TempDir Path tempDir) {
        testGenericValue(tempDir, "test", "test2");
    }

    @Test
    @DisplayName("Generic enum")
    void genericEnum(@TempDir Path tempDir) {
        testGenericValue(tempDir, TestEnum.TEST_1, TestEnum.TEST_2);
    }

    public static <T> ConfigEntry<T> testGenericValue(Path tempDir, T value1, T value2) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<T> entry = builder.entry("test", value1);
        TestUtils.finalizeBuilder(builder);

        assertEquals(value1, entry.get());
        entry.set(value2);
        // Set again to cover the case where the value is already set to the same value
        entry.set(value2).saveSync();
        assertEquals(value2, entry.get());

        TestUtils.reloadBuilder(builder);

        assertEquals(value2, entry.get());

        return entry;
    }

    enum TestEnum {
        TEST_1, TEST_2, TEST_3, TEST_4;
    }


}
