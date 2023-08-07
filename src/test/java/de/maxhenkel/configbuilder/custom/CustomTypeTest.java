package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.configbuilder.entry.GenericConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomTypeTest {

    @Test
    @DisplayName("Custom value")
    void customValue() {
        ConfigEntry<CustomType> entry = ConfigBuilder
                .builder(configBuilder -> configBuilder.entry("test", new CustomType("valid")))
                .addValueSerializer(CustomType.class, CustomTypeEntrySerializer.INSTANCE)
                .build();
        assertInstanceOf(GenericConfigEntry.class, entry);
        GenericConfigEntry<CustomType> genericEntry = (GenericConfigEntry<CustomType>) entry;
        assertNotNull(genericEntry.getSerializer());
        assertEquals("valid", entry.get().getValue());
        entry.set(new CustomType("invalid"));
        assertEquals("valid", entry.get().getValue());
        entry.set(new CustomType("valid2"));
        assertEquals("valid2", entry.get().getValue());
    }

    @Test
    @DisplayName("Custom value without registering")
    void customValueWithoutRegistering() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            ConfigBuilder
                    .builder(configBuilder -> configBuilder.entry("test", new CustomType("valid")))
                    .build();
        });
    }

    static class CustomType {
        private final String value;

        public CustomType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class CustomTypeEntrySerializer implements ValueSerializer<CustomType> {

        public static final CustomTypeEntrySerializer INSTANCE = new CustomTypeEntrySerializer();

        @Override
        public CustomType deserialize(String str) {
            if (str.equals("invalid")) {
                return null;
            }
            return new CustomType(str);
        }

        @Override
        public String serialize(CustomType val) {
            if (val.getValue().equals("invalid")) {
                return null;
            }
            return val.getValue();
        }
    }

}
