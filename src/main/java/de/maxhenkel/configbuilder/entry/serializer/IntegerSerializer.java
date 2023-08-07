package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class IntegerSerializer implements ValueSerializer<Integer> {

    public static final IntegerSerializer INSTANCE = new IntegerSerializer();

    @Nullable
    @Override
    public Integer deserialize(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String serialize(Integer val) {
        return String.valueOf(val);
    }

}
