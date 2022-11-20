package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseTest {

    @Test
    @DisplayName("Plain key value")
    void plainKeyValue() throws IOException {
        testInput("test=123", "test", "123");
    }

    @Test
    @DisplayName("Spaced out key value")
    void spacedOutKeyValue() throws IOException {
        testInput("test = 123", "test", "123");
        testInput("test : 123", "test", "123");
        testInput("test \t 123", "test", "123");
    }

    @Test
    @DisplayName("Comment")
    void comment() throws IOException {
        testInput("#Test\ntest=123", "test", "123");
        testInput("!Test\ntest=123", "test", "123");
    }

    @Test
    @DisplayName("Spaced Comment")
    void spacedComment() throws IOException {
        testInput(" #Test\ntest=123", "test", "123");
        testInput("   #Test\ntest=123", "test", "123");
        testInput("\t#Test\ntest=123", "test", "123");
        testInput("\t #Test\ntest=123", "test", "123");

        testInput(" !Test\ntest=123", "test", "123");
        testInput("   !Test\ntest=123", "test", "123");
        testInput("\t!Test\ntest=123", "test", "123");
        testInput("\t !Test\ntest=123", "test", "123");
    }

    @Test
    @DisplayName("Equals in key")
    void equalsInKey() throws IOException {
        testInput("te\\=st=123", "te=st", "123");
    }

    @Test
    @DisplayName("Backslash in key")
    void backslashInKey() throws IOException {
        testInput("te\\\\st=123", "te\\st", "123");
    }

    @Test
    @DisplayName("Tab in key")
    void tabInKey() throws IOException {
        testInput("te\\tst=123", "te\tst", "123");
    }

    @Test
    @DisplayName("Tab in value")
    void tabInValue() throws IOException {
        testInput("test=12\\t3", "test", "12\t3");
    }

    @Test
    @DisplayName("Unicode in key")
    void unicodeInKey() throws IOException {
        testInput("\\uD83D\\uDE02=123", "\uD83D\uDE02", "123");
    }

    @Test
    @DisplayName("Unicode in value")
    void unicodeInValue() throws IOException {
        testInput("test=\\uD83D\\uDE02", "test", "\uD83D\uDE02");
    }

    @Test
    @DisplayName("Backslash in value")
    void backslashInValue() throws IOException {
        testInput("test=12\\\\3", "test", "12\\3");
    }

    @Test
    @DisplayName("Equals in value")
    void equalsInValue() throws IOException {
        testInput("test=abc=def", "test", "abc=def");
    }

    @Test
    @DisplayName("Unescape equals")
    void unEscapeEquals() throws IOException {
        testInput("test=abc\\=def", "test", "abc=def");
    }

    @Test
    @DisplayName("Space at start of key")
    void spaceInKey() throws IOException {
        testInput(" test=123", "test", "123");
    }

    @Test
    @DisplayName("Escaped space at start of key")
    void escapedSpaceInKey() throws IOException {
        testInput("\\ test=123", " test", "123");
    }

    @Test
    @DisplayName("Escaped space at end of key")
    void escapedSpaceAtEndOfKey() throws IOException {
        testInput("test\\ =123", "test ", "123");
    }

    @Test
    @DisplayName("Space at start of value")
    void spaceAtStartOfValue() throws IOException {
        testInput("test= 123", "test", "123");
    }

    @Test
    @DisplayName("Space at end of value")
    void spaceAtEndOfValue() throws IOException {
        testInput("test=123 ", "test", "123 ");
    }

    @Test
    @DisplayName("Escaped tab at start of value")
    void escapedTabAtStartOfValue() throws IOException {
        testInput("test=\\t123", "test", "\t123");
    }

    @Test
    @DisplayName("Tab at start of value")
    void tabAtStartOfValue() throws IOException {
        testInput("test=\t123", "test", "123");
    }

    @Test
    @DisplayName("Escape value")
    void escapeValue() throws IOException {
        testInput("test=123\\=456", "test", "123=456");
    }

    @Test
    @DisplayName("Escaped new line")
    void escapedNewLine() throws IOException {
        testInput("test=123\\\n456", "test", "123456");
    }

    @Test
    @DisplayName("Colon key value")
    void colonKeyValue() throws IOException {
        testInput("test:123", "test", "123");
    }

    @Test
    @DisplayName("Space key value")
    void spaceKeyValue() throws IOException {
        testInput("test 123", "test", "123");
    }

    @Test
    @DisplayName("Space equals key value")
    void spaceEqualsKeyValue() throws IOException {
        testInput("test =123", "test", "123");
    }

    @Test
    @DisplayName("Space equals colon key value")
    void spaceEqualsColonKeyValue() throws IOException {
        testInputNoParity("test =:123", "test", "123");
        testInputNoParity("test: = 123", "test", "123");
        testInputNoParity("test:= 123", "test", "123");
        testInputNoParity("test:=123", "test", "123");
    }

    @Test
    @DisplayName("\\r at end of line")
    void backslashRAtEndOfLine() throws IOException {
        testInput("test=123\r", "test", "123");
    }

    private static void testInput(String input, String entryName, String expectedOutput) throws IOException {
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);
        assertEquals(expectedOutput, commentedProperties.get(entryName));

        ByteArrayInputStream in2 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Properties properties = new Properties();
        properties.load(in2);
        assertEquals(properties.get(entryName), commentedProperties.get(entryName), "Properties and CommentedProperties should be equal");
    }

    private static void testInputNoParity(String input, String entryName, String expectedOutput) throws IOException {
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);
        assertEquals(expectedOutput, commentedProperties.get(entryName));
    }

}
