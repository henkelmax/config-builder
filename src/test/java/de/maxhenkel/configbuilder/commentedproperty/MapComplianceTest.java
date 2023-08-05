package de.maxhenkel.configbuilder.commentedproperty;

import de.maxhenkel.configbuilder.CommentedProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MapComplianceTest {

    @Test
    @DisplayName("Map size")
    void mapSize() {
        CommentedProperties properties = new CommentedProperties();
        assertEquals(0, properties.size());
        properties.set("test", "123");
        assertEquals(1, properties.size());
        properties.set("test1", "123");
        assertEquals(2, properties.size());
    }

    @Test
    @DisplayName("Map isEmpty")
    void mapIsEmpty() {
        CommentedProperties properties = new CommentedProperties();
        assertTrue(properties.isEmpty());
        properties.set("test", "123");
        assertFalse(properties.isEmpty());
    }

    @Test
    @DisplayName("Map containsKey")
    void mapContainsKey() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertTrue(properties.containsKey("test"));
        assertFalse(properties.containsKey("test1"));
    }

    @Test
    @DisplayName("Map containsValue")
    void mapContainsValue() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertTrue(properties.containsValue("123"));
        assertFalse(properties.containsValue("456"));
    }

    @Test
    @DisplayName("Map get")
    void mapGet() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertEquals("123", properties.get("test"));
        assertNull(properties.get("test1"));
    }

    @Test
    @DisplayName("Map put")
    void mapPut() {
        CommentedProperties properties = new CommentedProperties();
        assertNull(properties.put("test", "123"));
        assertEquals("123", properties.put("test", "345"));
    }

    @Test
    @DisplayName("Map remove")
    void mapRemove() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertEquals("123", properties.remove("test"));
    }

    @Test
    @DisplayName("Map putAll")
    void mapPutAll() {
        CommentedProperties properties = new CommentedProperties();
        Map<String, String> map = new HashMap<>();
        map.put("test", "123");
        map.put("test1", "456");
        properties.putAll(map);
        assertEquals(2, properties.size());
        assertEquals("123", properties.get("test"));
        assertEquals("456", properties.get("test1"));
    }

    @Test
    @DisplayName("Map clear")
    void mapClear() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        properties.clear();
        assertNull(properties.get("test"));
    }

    @Test
    @DisplayName("Map keySet")
    void mapKeySet() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertTrue(properties.keySet().contains("test"));
    }

    @Test
    @DisplayName("Map values")
    void mapValues() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        assertTrue(properties.values().contains("123"));
    }

    @Test
    @DisplayName("Map entrySet")
    void mapEntrySet() {
        CommentedProperties properties = new CommentedProperties();
        properties.set("test", "123");
        Optional<Map.Entry<String, String>> any = properties.entrySet().stream().findAny();
        assertTrue(any.isPresent());
        assertEquals("test", any.get().getKey());
        assertEquals("123", any.get().getValue());
    }

}
