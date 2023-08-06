package de.maxhenkel.configbuilder.custom;

import de.maxhenkel.configbuilder.entry.EntrySerializable;
import de.maxhenkel.configbuilder.entry.ValueSerializer;

import java.util.UUID;

/**
 * A UUID wrapper that can be used as a config entry.
 */
@EntrySerializable(UUIDValue.UUIDValueSerializer.class)
public class UUIDValue {

    protected final UUID uuid;

    protected UUIDValue(UUID uuid) {
        this.uuid = uuid;
    }

    protected UUIDValue(String uuid) {
        this(UUID.fromString(uuid));
    }

    protected UUIDValue(long mostSigBits, long leastSigBits) {
        this(new UUID(mostSigBits, leastSigBits));
    }

    public static UUIDValue of(UUID uuid) {
        return new UUIDValue(uuid);
    }

    public static UUIDValue of(String uuid) {
        return new UUIDValue(uuid);
    }

    public static UUIDValue of(long mostSigBits, long leastSigBits) {
        return new UUIDValue(mostSigBits, leastSigBits);
    }

    public UUID getUuid() {
        return uuid;
    }

    static class UUIDValueSerializer implements ValueSerializer<UUIDValue> {
        @Override
        public UUIDValue deserialize(String str) {
            return UUIDValue.of(str);
        }

        @Override
        public String serialize(UUIDValue val) {
            return val.getUuid().toString();
        }
    }
}
