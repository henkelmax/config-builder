package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

public class StringConfigEntry extends AbstractConfigEntry<String> {

    public StringConfigEntry(CommentedPropertyConfig config, ValueSerializer<String> serializer, String[] comments, String key, String def) {
        super(config, serializer, comments, key, def);
        reload();
    }

}
