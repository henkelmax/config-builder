package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class EnumConfigEntry<T extends Enum> extends ConfigEntryImpl<Enum> {

    protected Class<T> enumClass;

    public EnumConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Enum def, Class<T> enumClass) {
        super(config, comments, key, def);
        this.enumClass = enumClass;
        reload();
    }

    @Nullable
    @Override
    public Enum deserialize(String str) {
        try {
            return Enum.valueOf(enumClass, str);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String serialize(Enum val) {
        return val.name();
    }
}
