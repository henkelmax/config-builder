package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.ConfigBuilderImpl;
import de.maxhenkel.configbuilder.TestUtils;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {

    @Test
    @DisplayName("String list")
    void stringList(@TempDir Path tempDir) throws IOException {
        GenericTypeTest.testGenericValue(tempDir, StringList.of(), StringList.of("test", "123", ",", "\\,", "\\\\,"));
        GenericTypeTest.testGenericValue(tempDir, StringList.of(), StringList.of(Arrays.asList("test", "123", ",", "\\,", "\\\\,")));

        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, "test=test\\\\,123".getBytes(StandardCharsets.UTF_8));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        ConfigEntry<StringList> entry = builder.entry("test", StringList.of());
        TestUtils.finalizeBuilder(builder);
        assertEquals(1, entry.get().size());
        assertEquals("test,123", entry.get().get(0));
    }

    @Test
    @DisplayName("Integer list")
    void integerList(@TempDir Path tempDir) throws IOException {
        GenericTypeTest.testGenericValue(tempDir, IntegerList.of(), IntegerList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0));
        GenericTypeTest.testGenericValue(tempDir, IntegerList.of(), IntegerList.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)));

        Path path = TestUtils.randomConfigName(tempDir);
        Files.write(path, "test=-100,100\ninvalid_test=1,2,a,3,4".getBytes(StandardCharsets.UTF_8));
        ConfigBuilderImpl builder = TestUtils.createBuilder(path);
        ConfigEntry<IntegerList> entry = builder.entry("test", IntegerList.of());
        ConfigEntry<IntegerList> invalidEntry = builder.entry("invalid_test", IntegerList.of());
        TestUtils.finalizeBuilder(builder);
        assertEquals(2, entry.get().size());
        assertEquals(-100, entry.get().get(0));
        assertEquals(100, entry.get().get(1));
        assertEquals(0, invalidEntry.get().size());
    }

    @Test
    @DisplayName("List compliance")
    void listCompliance() {
        StringList stringList = StringList.of("test", "123");

        assertEquals(2, stringList.size());
        assertFalse(stringList.isEmpty());
        assertTrue(stringList.contains("test"));
        assertNotNull(stringList.iterator());
        assertEquals(2, stringList.toArray().length);
        assertEquals(2, stringList.toArray(new String[2]).length);
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.add("123"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.remove("123"));
        assertTrue(stringList.containsAll(Arrays.asList("test", "123")));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.addAll(Arrays.asList("test", "123")));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.addAll(0, Arrays.asList("test", "123")));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.removeAll(Arrays.asList("test", "123")));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.retainAll(Arrays.asList("test", "123")));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.clear());
        assertEquals("test", stringList.get(0));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.set(0, "123"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.add(0, "123"));
        assertThrowsExactly(UnsupportedOperationException.class, () -> stringList.remove(0));
        assertEquals(0, stringList.indexOf("test"));
        assertEquals(0, stringList.lastIndexOf("test"));
        assertNotNull(stringList.listIterator());
        assertNotNull(stringList.listIterator(0));
        assertEquals(1, stringList.subList(0, 1).size());
        assertFalse(stringList.equals(null));
        assertFalse(stringList.equals(new Object()));
        assertDoesNotThrow(() -> stringList.hashCode());
    }

}
