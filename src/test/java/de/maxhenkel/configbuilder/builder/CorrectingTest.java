package de.maxhenkel.configbuilder.builder;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrectingTest {

    @Test
    @DisplayName("Integer bounds")
    void integerBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ninteger=20", "integer=10", builder -> {
            builder.integerEntry("integer", 0, -10, 10);
        });
        test(tempDir, "#test\ninteger=-20", "integer=-10", builder -> {
            builder.integerEntry("integer", 0, -10, 10);
        });
    }

    @Test
    @DisplayName("Invalid integer")
    void invalidInteger(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ninteger=1a", "integer=0", builder -> {
            builder.integerEntry("integer", 0, -10, 10);
        });
    }

    @Test
    @DisplayName("Long bounds")
    void longBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nlong=20", "long=10", builder -> {
            builder.longEntry("long", 0L, -10L, 10L);
        });
        test(tempDir, "#test\nlong=-20", "long=-10", builder -> {
            builder.longEntry("long", 0L, -10L, 10L);
        });
    }

    @Test
    @DisplayName("Invalid long")
    void invalidLong(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nlong=1a", "long=0", builder -> {
            builder.longEntry("long", 0L, -10L, 10L);
        });
    }

    @Test
    @DisplayName("Float bounds")
    void floatBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nfloat=20", "float=10.0", builder -> {
            builder.floatEntry("float", 0F, -10F, 10F);
        });
        test(tempDir, "#test\nfloat=-20", "float=-10.0", builder -> {
            builder.floatEntry("float", 0F, -10F, 10F);
        });
    }

    @Test
    @DisplayName("Invalid float")
    void invalidFloat(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nfloat=20.0a", "float=0.0", builder -> {
            builder.floatEntry("float", 0F, -10F, 10F);
        });
    }

    @Test
    @DisplayName("Double bounds")
    void doubleBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ndouble=20", "double=10.0", builder -> {
            builder.doubleEntry("double", 0D, -10D, 10D);
        });
        test(tempDir, "#test\ndouble=-20", "double=-10.0", builder -> {
            builder.doubleEntry("double", 0D, -10D, 10D);
        });
    }

    @Test
    @DisplayName("Invalid double")
    void invalidDouble(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ndouble=20.0a", "double=0.0", builder -> {
            builder.doubleEntry("double", 0D, -10D, 10D);
        });
    }

    @Test
    @DisplayName("Invalid enum")
    void invalidEnum(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nenum=TEST_0", "enum=TEST_2", builder -> {
            builder.enumEntry("enum", TestEnum.TEST_2);
        });
    }

    private void test(Path tempDir, String input, String expected, Consumer<ConfigBuilder> builderSupplier) throws IOException {
        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, input.getBytes());
        ConfigBuilder.builder(configBuilder -> {
            builderSupplier.accept(configBuilder);
            return null;
        }).path(path).saveSyncAfterBuild(true).build();
        assertEquals(expected, new String(Files.readAllBytes(path)).trim());
    }

    public enum TestEnum {
        TEST_1, TEST_2, TEST_3;
    }

}
