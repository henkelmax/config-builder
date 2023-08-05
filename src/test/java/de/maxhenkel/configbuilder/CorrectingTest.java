package de.maxhenkel.configbuilder;

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
    @DisplayName("Check integer bounds")
    void setIntegerBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ninteger=20", "integer=10", builder -> {
            builder.integerEntry("integer", 0, -10, 10);
        });
        test(tempDir, "#test\ninteger=-20", "integer=-10", builder -> {
            builder.integerEntry("integer", 0, -10, 10);
        });
    }

    @Test
    @DisplayName("Check long bounds")
    void setLongBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\nlong=20", "long=10", builder -> {
            builder.longEntry("long", 0L, -10L, 10L);
        });
        test(tempDir, "#test\nlong=-20", "long=-10", builder -> {
            builder.longEntry("long", 0L, -10L, 10L);
        });
    }

    @Test
    @DisplayName("Check double bounds")
    void setDoubleBounds(@TempDir Path tempDir) throws IOException {
        test(tempDir, "#test\ndouble=20", "double=10.0", builder -> {
            builder.doubleEntry("double", 0D, -10D, 10D);
        });
        test(tempDir, "#test\ndouble=-20", "double=-10.0", builder -> {
            builder.doubleEntry("double", 0D, -10D, 10D);
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

}
