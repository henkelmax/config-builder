package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.CommentedProperties;
import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MapTest {

    @Test
    @DisplayName("String map")
    void stringList(@TempDir Path tempDir) throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("test", "123");
        map.put("123", "test");
        GenericTypeTest.testGenericValue(tempDir, StringMap.of(), StringMap.of(map));
        GenericTypeTest.testGenericValue(tempDir, StringMap.of(), StringMap.builder().putAll(map).build());
        GenericTypeTest.testGenericValue(tempDir, StringMap.of(), StringMap.builder().put("test", "123").put("123", "test").build());
        GenericTypeTest.testGenericValue(tempDir, StringMap.of(), StringMap.builder().put("\"\"\"\\", "\"\"\"\\").put("123", "test").build());

        testMap(tempDir, "\"test\"=\"123\",\"123\"=\"test\"", StringMap.builder().put("test", "123").put("123", "test").build());
        testMap(tempDir, "\"test\"=\"1,2,3\"", StringMap.builder().put("test", "1,2,3").build());
        testMap(tempDir, "\"test\"=\"123\\\\\",\"test1\"=\"456\"", StringMap.builder().put("test", "123\\").put("test1", "456").build());
        testMap(tempDir, " , \"test\"=\"123\" , \"test1\"=\"456\" , ", "\"test\"=\"123\",\"test1\"=\"456\"", StringMap.builder().put("test", "123").put("test1", "456").build());
        testMap(tempDir, "\"test\" = \"123\",\"test1\"  =\"456\"", "\"test\"=\"123\",\"test1\"=\"456\"", StringMap.builder().put("test", "123").put("test1", "456").build());
        testMap(tempDir, " \"test\"=\"123\" \"test1\"=\"456\" ", "\"test\"=\"123\",\"test1\"=\"456\"", StringMap.builder().put("test", "123").put("test1", "456").build());

        testMap(tempDir, "\"test\"=\"123\" x \"123\"=\"test\"", "", StringMap.of());
    }

    private static void testMap(Path tempDir, String cfgValue, Map<String, String> map) throws IOException {
        testMap(tempDir, cfgValue, cfgValue, map);
    }

    private static void testMap(Path tempDir, String cfgValue, String cleanCfgValue, Map<String, String> map) throws IOException {
        Path path = TestUtils.randomConfigName(tempDir);
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", cfgValue);
        properties.save(Files.newOutputStream(path));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        ConfigEntry<StringMap> entry = builder.entry("test", StringMap.of());
        TestUtils.finalizeBuilder(builder);

        assertEquals(map.size(), entry.get().size());

        List<Map.Entry<String, String>> configEntryList = new ArrayList<>(entry.get().entrySet());
        List<Map.Entry<String, String>> realList = new ArrayList<>(map.entrySet());

        for (int i = 0; i < configEntryList.size(); i++) {
            assertEquals(realList.get(i).getKey(), configEntryList.get(i).getKey());
            assertEquals(realList.get(i).getValue(), configEntryList.get(i).getValue());
        }

        Path path2 = TestUtils.randomConfigName(tempDir);
        ConfigBuilderImpl builder2 = TestUtils.createBuilder(path2);
        ConfigEntry<StringMap> entry2 = builder2.entry("test", StringMap.of(map));
        TestUtils.finalizeBuilder(builder2);
        assertEquals(cleanCfgValue, entry2.getConfig().getEntries().get("test"));
    }

    @Test
    @DisplayName("Map compliance")
    void listCompliance() {
        StringMap stringMap = StringMap.builder().put("test", "123").put("123", "test").build();

        assertEquals(2, stringMap.size());
        assertFalse(stringMap.isEmpty());
        assertTrue(stringMap.containsKey("test"));
        assertTrue(stringMap.containsValue("123"));
        assertEquals("123", stringMap.get("test"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringMap.put("test", "123"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringMap.remove("test"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringMap.putAll(Collections.emptyMap()));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringMap.clear());
        assertEquals(2, stringMap.keySet().size());
        assertEquals(2, stringMap.values().size());
        assertEquals(2, stringMap.entrySet().size());
        assertFalse(stringMap.equals(null));
        assertFalse(stringMap.equals(new Object()));
        assertDoesNotThrow(() -> stringMap.hashCode());
    }

}
