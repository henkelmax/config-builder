package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class FileErrorTest {

    @BeforeAll
    static void beforeAll() {
        Logger.getLogger(CommentedPropertyConfig.class.getName()).setLevel(Level.OFF);
    }

    @Test
    @DisplayName("Exception when reloading")
    void reload(@TempDir Path tempDir) throws IOException {
        CommentedPropertyConfig config = new CommentedPropertyConfig(TestUtils.randomConfigName(tempDir));

        CommentedPropertyConfig spyConfig = spy(config);
        doThrow(IOException.class).when(spyConfig).load();

        assertDoesNotThrow(spyConfig::reload);
    }

    @Test
    @DisplayName("Exception when creating parent directory")
    void parentDirectory(@TempDir Path tempDir) {
        Path path = TestUtils.randomConfigName(tempDir);

        Path mockPath = spy(path);
        doReturn(mockPath).when(mockPath).toAbsolutePath();
        doThrow(UncheckedIOException.class).when(mockPath).getParent();

        CommentedPropertyConfig config = new CommentedPropertyConfig(mockPath);

        assertDoesNotThrow(config::saveSync);
    }

    @Test
    @DisplayName("Exception when saving")
    void save(@TempDir Path tempDir) throws NoSuchFieldException, IllegalAccessException {
        Path path = TestUtils.randomConfigName(tempDir);
        CommentedPropertyConfig config = new CommentedPropertyConfig(path);
        Field propertiesField = CommentedPropertyConfig.class.getDeclaredField("properties");
        propertiesField.setAccessible(true);
        CommentedProperties properties = spy((CommentedProperties) propertiesField.get(config));
        propertiesField.set(config, properties);

        doThrow(UncheckedIOException.class).when(properties).save(any());

        assertDoesNotThrow(config::saveSync);
    }

}
