package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapeTest {

    @Test
    @DisplayName("Plain key value")
    void plainKeyValue() throws IOException {
        testKeyValue("test", "123", "test=123");
    }

    @Test
    @DisplayName("Double backslash")
    void doubleBackslash() throws IOException {
        testKeyValue("test", "\\\\t", "test=\\\\\\\\t");
    }

    @Test
    @DisplayName("Space in key")
    void spaceInKey() throws IOException {
        testKeyValue("te st", "123", "te\\ st=123");
        testKeyValue(" test", "123", "\\ test=123");
        testKeyValue("test ", "123", "test\\ =123");
    }

    @Test
    @DisplayName("Space in value")
    void spaceInValue() throws IOException {
        testKeyValue("test", " 123", "test=\\ 123");
        testKeyValue("test", "1 23", "test=1 23");
        testKeyValue("test", "123 ", "test=123 ");
    }

    @Test
    @DisplayName("Equals in key")
    void equalsInKey() throws IOException {
        testKeyValue("te=st", "123", "te\\=st=123");
        testKeyValue("=test", "123", "\\=test=123");
        testKeyValue("test=", "123", "test\\==123");
    }

    @Test
    @DisplayName("Equals in value")
    void equalsInValue() throws IOException {
        testKeyValue("test", "1=23", "test=1\\=23");
        testKeyValue("test", "=123", "test=\\=123");
        testKeyValue("test", "123=", "test=123\\=");
    }

    @Test
    @DisplayName("Backslash in key")
    void backslashInKey() throws IOException {
        testKeyValue("te\\st", "123", "te\\\\st=123");
        testKeyValue("\\test", "123", "\\\\test=123");
        testKeyValue("test\\", "123", "test\\\\=123");
    }

    @Test
    @DisplayName("Backslash in value")
    void backslashInValue() throws IOException {
        testKeyValue("test", "1\\23", "test=1\\\\23");
        testKeyValue("test", "\\123", "test=\\\\123");
        testKeyValue("test", "123\\", "test=123\\\\");
    }

    @Test
    @DisplayName("Tab in key")
    void tabInKey() throws IOException {
        testKeyValue("te\tst", "123", "te\\tst=123");
        testKeyValue("\ttest", "123", "\\ttest=123");
        testKeyValue("test\t", "123", "test\\t=123");
    }

    @Test
    @DisplayName("Tab in value")
    void tabInValue() throws IOException {
        testKeyValue("test", "1\t23", "test=1\\t23");
        testKeyValue("test", "\t123", "test=\\t123");
        testKeyValue("test", "123\t", "test=123\\t");
    }

    @Test
    @DisplayName("Newline in key")
    void newlineInKey() throws IOException {
        testKeyValue("te\nst", "123", "te\\nst=123");
        testKeyValue("\ntest", "123", "\\ntest=123");
        testKeyValue("test\n", "123", "test\\n=123");
    }

    @Test
    @DisplayName("Newline in value")
    void newlineInValue() throws IOException {
        testKeyValue("test", "1\n23", "test=1\\n23");
        testKeyValue("test", "\n123", "test=\\n123");
        testKeyValue("test", "123\n", "test=123\\n");
    }

    @Test
    @DisplayName("Unicode in key")
    void unicodeInKey() throws IOException {
        testKeyValue("te\uD83D\uDE02st", "123", "te\\uD83D\\uDE02st=123");
        testKeyValue("\uD83D\uDE02test", "123", "\\uD83D\\uDE02test=123");
        testKeyValue("test\uD83D\uDE02", "123", "test\\uD83D\\uDE02=123");
    }

    @Test
    @DisplayName("Unicode in value")
    void unicodeInValue() throws IOException {
        testKeyValue("test", "1\uD83D\uDE0223", "test=1\\uD83D\\uDE0223");
        testKeyValue("test", "\uD83D\uDE02123", "test=\\uD83D\\uDE02123");
        testKeyValue("test", "123\uD83D\uDE02", "test=123\\uD83D\\uDE02");
        testKeyValue("test", "°§╓", "test=\\u00B0\\u00A7\\u2553");
        testKeyValue("test", "!\"$%&/()=?`*+'#-_.:,;|><^", "test=\\!\"$%&/()\\=?`*+'\\#-_.\\:,;|><^");
    }

    @Test
    @DisplayName("Comment character in key")
    void commentCharacterInKey() throws IOException {
        testKeyValue("te#st", "123", "te\\#st=123");
        testKeyValue("#test", "123", "\\#test=123");
        testKeyValue("test#", "123", "test\\#=123");
    }

    @Test
    @DisplayName("Comment character in value")
    void commentCharacterInValue() throws IOException {
        testKeyValue("test", "1#23", "test=1\\#23");
        testKeyValue("test", "#123", "test=\\#123");
        testKeyValue("test", "123#", "test=123\\#");
    }

    @Test
    @DisplayName("Separator character in key")
    void separatorCharacterInKey() throws IOException {
        testKeyValue("te=st", "123", "te\\=st=123");
        testKeyValue("=test", "123", "\\=test=123");
        testKeyValue("test=", "123", "test\\==123");
        testKeyValue("te:st", "123", "te\\:st=123");
        testKeyValue(":test", "123", "\\:test=123");
        testKeyValue("test:", "123", "test\\:=123");
    }

    @Test
    @DisplayName("Separator character in value")
    void separatorCharacterInValue() throws IOException {
        testKeyValue("test", "1=23", "test=1\\=23");
        testKeyValue("test", "=123", "test=\\=123");
        testKeyValue("test", "123=", "test=123\\=");
        testKeyValue("test", "1:23", "test=1\\:23");
        testKeyValue("test", ":123", "test=\\:123");
        testKeyValue("test", "123:", "test=123\\:");
    }

    private static void testKeyValue(String key, String value, String expectedOutput) throws IOException {
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.set(key, value);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        commentedProperties.save(stream);
        String output = Arrays.stream(stream.toString().replace("\r\n", "\n").split("\n")).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, output);

        Properties properties = new Properties();
        properties.put(key, value);
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        properties.store(stream1, "");
        String output1 = Arrays.stream(stream1.toString().replace("\r\n", "\n").split("\n")).skip(2).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, output1, "Properties and CommentedProperties should be equal");
    }

    private static void testKeyValueNoParity(String key, String value, String expectedOutput) throws IOException {
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.set(key, value);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        commentedProperties.save(stream);
        String output = Arrays.stream(stream.toString().replace("\r\n", "\n").split("\n")).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, output);
    }

}
