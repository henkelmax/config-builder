package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidTest {

    @Test
    @DisplayName("Nonexistent entry")
    void nonexistentEntry() {
        CommentedProperties properties = new CommentedProperties();
        assertNull(properties.get("test"));
    }

    @Test
    @DisplayName("Invalid (too short) unicode escape sequence")
    void invalidUnicodeEscape1() {
        String input = "test=\\u123";
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        assertThrowsExactly(IOException.class, () -> commentedProperties.load(in1));
    }

    @Test
    @DisplayName("Invalid (invalid character) unicode escape sequence")
    void invalidUnicodeEscape2() {
        String input = "test=\\u123Z";
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        assertThrowsExactly(IOException.class, () -> commentedProperties.load(in1));
    }

}
