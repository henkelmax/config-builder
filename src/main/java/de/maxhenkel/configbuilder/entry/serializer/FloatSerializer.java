package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class FloatSerializer implements ValueSerializer<Float> {

    public static final FloatSerializer INSTANCE = new FloatSerializer();

    @Nullable
    @Override
    public Float deserialize(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String serialize(Float val) {
        return String.valueOf(val);
    }

}
