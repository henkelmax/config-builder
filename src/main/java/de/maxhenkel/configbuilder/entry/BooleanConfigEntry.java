package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class BooleanConfigEntry extends AbstractConfigEntry<Boolean> {

    public BooleanConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Boolean def) {
        super(config, comments, key, def);
        reload();
    }

    @Override
    @Nullable
    public Boolean deserialize(String str) {
        return Boolean.valueOf(str);
    }

    @Override
    public String serialize(Boolean val) {
        return String.valueOf(val);
    }
}
