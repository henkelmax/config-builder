package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.setHeaderComments(Arrays.asList(headerComments));
        commentedProperties.set(key, value, comments);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        commentedProperties.save(stream);
        String output = stream.toString().replace("\r\n", "\n");
        assertEquals(String.join("\n", expectedOutput), output);
    }

}
