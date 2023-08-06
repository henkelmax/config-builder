package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnicodeTest {

    @Test
    @DisplayName("All characters")
    void allCharacters() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int codePoint = 0; codePoint < 4096; codePoint++) {
            switch (Character.getType(codePoint)) {
                case Character.UNASSIGNED:
                case Character.CONTROL:
                case Character.FORMAT:
                    break;
                default:
                    sb.append(Character.toChars(codePoint));
            }
        }
        testInputOutput("test", sb.toString());
        testInputOutput(sb.toString(), "test");
        testInputOutput(sb.toString(), sb.toString());
    }

    private static void testInputOutput(String key, String value) throws IOException {
        CommentedProperties commentedPropertiesIn = new CommentedProperties();
        commentedPropertiesIn.set(key, value);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        commentedPropertiesIn.save(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        CommentedProperties commentedPropertiesOut = new CommentedProperties();
        commentedPropertiesOut.load(in);
        assertEquals(value, commentedPropertiesOut.get(key), String.format("Input '%s' for key '%s' did not match output '%s'", value, key, commentedPropertiesOut.get(key)));
    }

}
