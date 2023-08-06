package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.builder.GenericTypeTest;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDTest {

    @Test
    @DisplayName("Builder")
    void builder(@TempDir Path tempDir) throws IOException {
        GenericTypeTest.testGenericValue(tempDir, UUIDValue.of(0L, 0L), UUIDValue.of("affa4e1e-8c9a-4a9d-9b1a-1b9f7e9e1a0a"));
        GenericTypeTest.testGenericValue(tempDir, UUIDValue.of(0L, 0L), UUIDValue.of(UUID.randomUUID()));

        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, "test=affa4e1e-8c9a-4a9d-9b1a-1b9f7e9e1a0a".getBytes(StandardCharsets.UTF_8));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        ConfigEntry<UUIDValue> entry = builder.entry("test", UUIDValue.of(0L, 0L));
        TestUtils.finalizeBuilder(builder);
        assertEquals(UUID.fromString("affa4e1e-8c9a-4a9d-9b1a-1b9f7e9e1a0a"), entry.get().getUuid());
    }

}
