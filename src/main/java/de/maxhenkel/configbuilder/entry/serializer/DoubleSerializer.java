package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class DoubleSerializer implements ValueSerializer<Double> {

    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    @Nullable
    @Override
    public Double deserialize(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String serialize(Double val) {
        return String.valueOf(val);
    }

}
