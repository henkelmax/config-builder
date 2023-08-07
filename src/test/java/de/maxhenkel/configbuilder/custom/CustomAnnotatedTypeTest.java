package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.EntrySerializable;
import de.maxhenkel.configbuilder.entry.GenericConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class CustomAnnotatedTypeTest {

    @Test
    @DisplayName("Custom value")
    void customValue(@TempDir Path tempDir) {
        ConfigEntry<CustomType> entry = GenericTypeTest.testGenericValue(tempDir, new CustomType("test1"), new CustomType("test2"));
        assertInstanceOf(GenericConfigEntry.class, entry);
        GenericConfigEntry<CustomType> genericEntry = (GenericConfigEntry<CustomType>) entry;
        assertNotNull(genericEntry.getSerializer());
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
            GenericTypeTest.testGenericValue(tempDir, new CustomTypeWithoutAnnotation("test1"), new CustomTypeWithoutAnnotation("test2"));
        });
    }

    @Test
    @DisplayName("Invalid serializer")
    void invalidSerializer(@TempDir Path tempDir) {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            GenericTypeTest.testGenericValue(tempDir, new CustomTypeWithInvalidSerializer("test1"), new CustomTypeWithInvalidSerializer("test2"));
        });
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

    static class CustomTypeEntrySerializer implements ValueSerializer<CustomType> {
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

    static class NullCustomTypeEntrySerializer implements ValueSerializer<NullCustomType> {
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

    static class InvalidSerializer implements ValueSerializer<CustomType> {

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
