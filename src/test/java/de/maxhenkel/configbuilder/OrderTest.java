package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    @Test
    @DisplayName("Save and read")
    void saveAndRead(@TempDir Path tempDir) throws IOException {
        Path config = TestUtils.randomConfigName(tempDir);

        StringBuilder configString = new StringBuilder();
        configString.append("test_three=true");
        configString.append("\n");
        configString.append("test_two=two");
        configString.append("\n");
        configString.append("test_one=1");
        configString.append("\n");
        configString.append("unused1=nothing");
        configString.append("\n");
        configString.append("unused2=nothing");

        ByteArrayInputStream in1 = new ByteArrayInputStream(configString.toString().getBytes(StandardCharsets.UTF_8));
        CommentedProperties commentedProperties = new CommentedProperties();
        commentedProperties.load(in1);
        commentedProperties.save(Files.newOutputStream(config.toFile().toPath()));

        ConfigBuilder.build(config, false, builder -> {
            ConfigEntry<Integer> one = builder.integerEntry("test_one", 1, 0, 10);
            ConfigEntry<String> two = builder.stringEntry("test_two", "two");
            ConfigEntry<Boolean> three = builder.booleanEntry("test_three", true);
            return null;
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ConfigBuilderImpl builder = TestUtils.createBuilder(config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        builder.config.properties.save(baos);

        StringBuilder expectedOutput = new StringBuilder();
        expectedOutput.append("test_one=1");
        expectedOutput.append("\n");
        expectedOutput.append("test_two=two");
        expectedOutput.append("\n");
        expectedOutput.append("test_three=true");
        expectedOutput.append("\n");
        expectedOutput.append("unused1=nothing");
        expectedOutput.append("\n");
        expectedOutput.append("unused2=nothing");

        assertEquals(expectedOutput.toString().replace("\r\n", "\n").trim(), baos.toString().replace("\r\n", "\n").trim());
    }

}
