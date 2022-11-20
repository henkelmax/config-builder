package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompatibilityTest {

    @Test
    @DisplayName("Test compatibility")
    void testCompatibility() throws IOException {
        testProperties(
                "test1=123\\",
                "456",
                "test2=|$()%$*",
                "test3=\\uD83D\\uDE02",
                "test4",
                "test5: 123",
                "test6 = 123",
                "test7 = 123    ",
                "test8 = 123    #123",
                "# Comment 1",
                "test9 = 123    !123",
                "! Comment 2",
                "",
                "test10 = 123    #123",
                "",
                "",
                "! test = 123",
                "# test : 123",
                "   ! test = 123",
                "   # test : 123",
                " ! test = 123",
                " # test : 123",
                "                           #",
                "test123#123",
                "test11",
                "test12 = 123\\",
                "test13 = 123"
        );
    }

    private static void testProperties(String... lines) throws IOException {
        String propertyString = String.join("\n", lines);
        ByteArrayInputStream in1 = new ByteArrayInputStream(propertyString.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);

        ByteArrayInputStream in2 = new ByteArrayInputStream(propertyString.getBytes(StandardCharsets.UTF_8));
        Properties properties = new Properties();
        properties.load(in2);

        for (Object key : properties.keySet()) {
            if (key instanceof String) {
                assertEquals(properties.get(key), commentedProperties.get((String) key), String.format("Key '%s' does not have the same value in commented properties", key));
            } else {
                throw new IllegalStateException("Key is not a string");
            }
        }
        for (String key : commentedProperties.keySet()) {
            Object value = properties.get(key);
            if (value == null) {
                throw new IllegalStateException(String.format("Unknown key '%s'", key));
            }
            assertEquals(value, commentedProperties.get(key), String.format("Key '%s' does not have the same value in commented properties", key));
        }
    }

}
