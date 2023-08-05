package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentedPropertyConfigTest {

    @Test
    @DisplayName("Old constructor")
    void oldConstructor(@TempDir Path tempDir) {
        CommentedPropertyConfig config = new CommentedPropertyConfig(TestUtils.randomConfigName(tempDir));

        config.set("test", "123");
        assertEquals("123", config.get("test"));
    }

    @Test
    @DisplayName("Old constructor with null")
    void oldConstructorNull() {
        CommentedPropertyConfig config = new CommentedPropertyConfig(null);

        config.set("test", "123");
        assertEquals("123", config.get("test"));
    }

}