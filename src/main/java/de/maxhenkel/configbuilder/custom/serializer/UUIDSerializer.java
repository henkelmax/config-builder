package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import java.util.UUID;

public class UUIDSerializer implements ValueSerializer<UUID> {

    @Override
    public UUID deserialize(String str) {
        return UUID.fromString(str);
    }

    @Override
    public String serialize(UUID val) {
        return val.toString();
    }
}
