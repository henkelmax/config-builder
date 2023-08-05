package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class EnumConfigEntry<E extends Enum<E>> extends ConfigEntryImpl<E> {

    protected Class<E> enumClass;

    public EnumConfigEntry(CommentedPropertyConfig config, String[] comments, String key, E def, Class<E> enumClass) {
        super(config, comments, key, def);
        this.enumClass = enumClass;
        reload();
    }

    @Nullable
    @Override
    E deserialize(String str) {
        try {
            return Enum.valueOf(enumClass, str);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    String serialize(E val) {
        return val.name();
    }
}
