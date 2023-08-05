package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class StringConfigEntry extends AbstractConfigEntry<String> {

    public StringConfigEntry(CommentedPropertyConfig config, String[] comments, String key, String def) {
        super(config, comments, key, def);
        reload();
    }

    @Override
    @Nullable
    String deserialize(String str) {
        return str;
    }

    @Override
    String serialize(String val) {
        return val;
    }
}
