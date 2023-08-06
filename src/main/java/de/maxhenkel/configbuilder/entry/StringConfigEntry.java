package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class StringConfigEntry extends AbstractConfigEntry<String> {

    public StringConfigEntry(CommentedPropertyConfig config, String[] comments, String key, String def) {
        super(config, comments, key, def);
        reload();
    }

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
