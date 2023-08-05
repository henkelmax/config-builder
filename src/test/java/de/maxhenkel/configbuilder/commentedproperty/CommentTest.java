package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    @DisplayName("Test comments")
    void testComments() throws IOException {
        testComments(new String[]{"Header 1", "Header 2"}, "test", "123", new String[]{"Description 1", "Description 2"},
                "# Header 1",
                "# Header 2",
                "",
                "# Description 1",
                "# Description 2",
                "test=123",
                ""
        );
    }

    @Test
    @DisplayName("Test newline comment")
    void testNewlineComments() throws IOException {
        testComments(new String[]{"Header 1\nHeader 2", "Header 3"}, "test", "123", new String[]{"Description 1\nDescription 2", "Description 3"},
                "# Header 1",
                "# Header 2",
                "# Header 3",
                "",
                "# Description 1",
                "# Description 2",
                "# Description 3",
                "test=123",
                ""
        );
    }

    @Test
    @DisplayName("Test no header comments")
    void testNoHeaderComments() throws IOException {
        testComments(new String[]{}, "test", "123", new String[]{"Description 1", "Description 2"},
                "# Description 1",
                "# Description 2",
                "test=123",
                ""
        );
    }

    @Test
    @DisplayName("Test no comments")
    void testNoComments() throws IOException {
        testComments(new String[]{}, "test", "123", new String[]{},
                "test=123",
                ""
        );
    }

    private static void testComments(String[] headerComments, String key, String value, String[] comments, String... expectedOutput) throws IOException {
        CommentedProperties properties = new CommentedProperties();
        properties.setHeaderComments(Arrays.asList(headerComments));
        properties.set(key, value, comments);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        properties.save(stream);
        String output = stream.toString().replace("\r\n", "\n");
        assertEquals(String.join("\n", expectedOutput), output);
    }

    @Test
    @DisplayName("Test get comments")
    void testGetComments() {
        CommentedProperties properties = new CommentedProperties();

        properties.addHeaderComment("Header 1");

        properties.set("test", "123", "Test comment");
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        properties.save(stream2);
        String output = stream2.toString().replace("\r\n", "\n");

        assertEquals("# Header 1\n\n# Test comment\ntest=123\n", output);

        assertLinesMatch(Collections.singletonList("Test comment"), properties.getComments("test"));
    }

    @Test
    @DisplayName("Get comment of nonexistent property")
    void getNonexistentComment() {
        CommentedProperties properties = new CommentedProperties();
        assertNull(properties.getComments("test"));
    }

    @Test
    @DisplayName("Set comment of nonexistent property")
    void setNonexistentComment() {
        CommentedProperties properties = new CommentedProperties();
        properties.setComments("test", Collections.singletonList("Test comment"));
        assertLinesMatch(Collections.singletonList("Test comment"), properties.getComments("test"));
        assertEquals("", properties.get("test"));
    }

}
