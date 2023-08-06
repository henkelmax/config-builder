package de.maxhenkel.configbuilder.builder;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.configbuilder.entry.EntrySerializable;
import de.maxhenkel.configbuilder.entry.EntrySerializer;
import de.maxhenkel.configbuilder.entry.GenericConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Custom value")
    void customValue(@TempDir Path tempDir) {
        ConfigEntry<CustomType> entry = testGenericValue(tempDir, new CustomType("test1"), new CustomType("test2"));
        assertInstanceOf(GenericConfigEntry.class, entry);
        GenericConfigEntry<CustomType> genericEntry = (GenericConfigEntry<CustomType>) entry;
        assertNotNull(genericEntry.getEntrySerializer());
    }

    @Test
    @DisplayName("Save with null value")
    void saveNull(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        assertThrowsExactly(IllegalStateException.class, () -> {
            builder.entry("test", new NullCustomType("test1"));
        });
    }

    @Test
    @DisplayName("Load with invalid value")
    void loadInvalid(@TempDir Path tempDir) throws IOException {
        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, Collections.singletonList("test=invalid"));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        ConfigEntry<CustomType> entry = builder.entry("test", new CustomType("valid"));
        TestUtils.finalizeBuilder(builder);

        assertEquals(new CustomType("valid"), entry.get());
    }

    @Test
    @DisplayName("Set invalid value")
    void setInvalid(@TempDir Path tempDir) {
        ConfigBuilderImpl builder = TestUtils.createBuilderWithRandomPath(tempDir);
        ConfigEntry<CustomType> entry = builder.entry("test", new CustomType("valid"));
        TestUtils.finalizeBuilder(builder);
        entry.set(new CustomType("invalid"));
        assertEquals(new CustomType("valid"), entry.get());
    }

    @Test
    @DisplayName("Load with invalid value and default invalid value")
    void loadInvalidDefaultInvalid(@TempDir Path tempDir) throws IOException {
        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, Collections.singletonList("test=invalid"));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        assertThrowsExactly(IllegalStateException.class, () -> {
            builder.entry("test", new CustomType("invalid"));
        });
    }

    @Test
    @DisplayName("Load with null value")
    void loadNull(@TempDir Path tempDir) throws IOException {
        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, Collections.singletonList("test=test2"));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        assertThrowsExactly(IllegalStateException.class, () -> {
            builder.entry("test", new NullCustomType("test1"));
        });
    }

    @Test
    @DisplayName("No annotation")
    void noAnnotation(@TempDir Path tempDir) {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            testGenericValue(tempDir, new CustomTypeWithoutAnnotation("test1"), new CustomTypeWithoutAnnotation("test2"));
        });
    }

    @Test
    @DisplayName("Invalid serializer")
    void invalidSerializer(@TempDir Path tempDir) {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            testGenericValue(tempDir, new CustomTypeWithInvalidSerializer("test1"), new CustomTypeWithInvalidSerializer("test2"));
        });
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

    @EntrySerializable(CustomTypeEntrySerializer.class)
    static class CustomType {
        private final String value;

        public CustomType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CustomType that = (CustomType) o;

            return Objects.equals(value, that.value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static class CustomTypeEntrySerializer implements EntrySerializer<CustomType> {
        @Override
        public CustomType deserialize(String str) {
            if (str.equals("invalid")) {
                return null;
            }
            return new CustomType(str);
        }

        @Override
        public String serialize(CustomType val) {
            if (val.getValue().equals("invalid")) {
                return null;
            }
            return val.getValue();
        }
    }

    @EntrySerializable(NullCustomTypeEntrySerializer.class)
    static class NullCustomType {
        private final String value;

        public NullCustomType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NullCustomType that = (NullCustomType) o;

            return Objects.equals(value, that.value);
        }
    }

    static class NullCustomTypeEntrySerializer implements EntrySerializer<NullCustomType> {
        @Override
        public NullCustomType deserialize(String str) {
            return null;
        }

        @Override
        public String serialize(NullCustomType val) {
            return null;
        }
    }

    static class CustomTypeWithoutAnnotation {
        private final String value;

        public CustomTypeWithoutAnnotation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @EntrySerializable(InvalidSerializer.class)
    static class CustomTypeWithInvalidSerializer {
        private final String value;

        public CustomTypeWithInvalidSerializer(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class InvalidSerializer implements EntrySerializer<CustomType> {

        public InvalidSerializer(String str) {

        }

        @Override
        public CustomType deserialize(String str) {
            return new CustomType(str);
        }

        @Override
        public String serialize(CustomType val) {
            return val.getValue();
        }
    }

}
