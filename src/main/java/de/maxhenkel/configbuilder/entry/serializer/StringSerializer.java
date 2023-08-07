package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class StringSerializer implements ValueSerializer<String> {

    public static final StringSerializer INSTANCE = new StringSerializer();

    @Nullable
    @Override
    public String deserialize(String str) {
        return str;
    }

    @Nullable
    @Override
    public String serialize(String val) {
        return val;
    }

}
