package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertTest {

    @Test
    @DisplayName("Plain key value")
    void plainKeyValue() throws IOException {
        testConversion("test=123", "test=123");
        testConversion("test = 123", "test=123");
        testConversion("test =123", "test=123");
        testConversion("test= 123", "test=123");
    }

    @Test
    @DisplayName("Colon key value")
    void colonKeyValue() throws IOException {
        testConversion("test:123", "test=123");
        testConversion("test : 123", "test=123");
        testConversion("test :123", "test=123");
        testConversion("test: 123", "test=123");
    }

    @Test
    @DisplayName("Space key value")
    void spaceKeyValue() throws IOException {
        testConversion("test 123", "test=123");
        testConversion("test  123", "test=123");
        testConversion("test   123", "test=123");
    }

    @Test
    @DisplayName("Tab key value")
    void tabKeyValue() throws IOException {
        testConversion("test\t123", "test=123");
        testConversion("test \t 123", "test=123");
        testConversion("test \t123", "test=123");
        testConversion("test\t 123", "test=123");
    }

    @Test
    @DisplayName("Equals in value")
    void equalsInValue() throws IOException {
        testConversion("test=1=23", "test=1\\=23");
    }

    @Test
    @DisplayName("Comments")
    void comments() throws IOException {
        testConversionNoParity("# Title\n\n# Test\ntest=123", "# Title\n\n# Test\ntest=123\n");
        testConversionNoParity("# Title\n# Title 2\n\n# Test\n\ntest=123", "# Title\n# Title 2\n\n# Test\ntest=123\n");
        testConversionNoParity("#Test\ntest=123", "# Test\ntest=123\n");
        testConversionNoParity("#  Test\ntest=123", "# Test\ntest=123\n");
        testConversionNoParity("#  \t\t  Test\ntest=123", "# Test\ntest=123\n");

        testConversionNoParity("! Title\n\n! Test\ntest=123", "# Title\n\n# Test\ntest=123\n");
        testConversionNoParity("! Title\n! Title 2\n\n! Test\n\ntest=123", "# Title\n# Title 2\n\n# Test\ntest=123\n");
        testConversionNoParity("!Test\ntest=123", "# Test\ntest=123\n");
        testConversionNoParity("!  Test\ntest=123", "# Test\ntest=123\n");
        testConversionNoParity("!  \t\t  Test\ntest=123", "# Test\ntest=123\n");

        testConversionNoParity("test=123 # Test", "test=123 \\# Test\n");
    }

    @Test
    @DisplayName("Separator and comment")
    void separatorAndComment() throws IOException {
        testConversionNoParity("=#Test\ntest=123", "=\\#Test\ntest=123\n");
        testConversionNoParity("\\ =#Test\ntest=123", "\\ =\\#Test\ntest=123\n");
    }

    private static void testConversion(String input, String expectedOutput) throws IOException {
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        commentedProperties.save(out1);
        assertEquals(expectedOutput, Arrays.stream(out1.toString().replace("\r\n", "\n").split("\n")).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining("\n")));

        ByteArrayInputStream in2 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Properties properties = new Properties();
        properties.load(in2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        properties.store(out2, "");
        String output1 = Arrays.stream(out2.toString().replace("\r\n", "\n").split("\n")).skip(2).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining("\n"));
        assertEquals(expectedOutput, output1, "Properties and CommentedProperties should be equal");
    }

    private static void testConversionNoParity(String input, String expectedOutput) throws IOException {
        ByteArrayInputStream in1 = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        commentedProperties.save(out1);
        assertEquals(expectedOutput, out1.toString().replace("\r\n", "\n"));
    }

}
