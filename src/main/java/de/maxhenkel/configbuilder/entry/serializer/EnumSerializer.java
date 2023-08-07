package de.maxhenkel.configbuilder.entry.serializer;

import javax.annotation.Nullable;

public class EnumSerializer<E extends Enum<E>> implements ValueSerializer<E> {

    protected Class<E> enumClass;

    public EnumSerializer(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Nullable
    @Override
    public E deserialize(String str) {
        try {
            return Enum.valueOf(enumClass, str);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String serialize(E val) {
        return val.name();
    }

}
