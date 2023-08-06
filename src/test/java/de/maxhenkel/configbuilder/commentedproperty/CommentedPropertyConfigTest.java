package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentedPropertyConfigTest {

    @Test
    @DisplayName("Set value")
    void setValue() {
        CommentedPropertyConfig config = CommentedPropertyConfig.builder().build();

        config.set("test", "123");
        assertEquals("123", config.get("test"));
    }

    @Test
    @DisplayName("Set null key")
    void setNullKey() {
        CommentedPropertyConfig config = CommentedPropertyConfig.builder().build();
        assertThrowsExactly(NullPointerException.class, () -> {
            config.set(null, "123");
        });
    }

    @Test
    @DisplayName("Set null value")
    void setNullValue() {
        CommentedPropertyConfig config = CommentedPropertyConfig.builder().build();
        assertThrowsExactly(NullPointerException.class, () -> {
            config.set("test", null);
        });
    }

    @Test
    @DisplayName("Get null")
    void getNull() {
        CommentedPropertyConfig config = CommentedPropertyConfig.builder().build();
        assertNull(config.get("test"));
    }

}
