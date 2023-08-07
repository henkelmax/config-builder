package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class BooleanSerializer implements ValueSerializer<Boolean> {

    public static final BooleanSerializer INSTANCE = new BooleanSerializer();

    @Nullable
    @Override
    public Boolean deserialize(String str) {
        return Boolean.valueOf(str);
    }

    @Nullable
    @Override
    public String serialize(Boolean val) {
        return String.valueOf(val);
    }

}
