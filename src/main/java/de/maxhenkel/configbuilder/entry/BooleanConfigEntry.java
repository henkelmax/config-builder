package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

public class BooleanConfigEntry extends AbstractConfigEntry<Boolean> {

    public BooleanConfigEntry(CommentedPropertyConfig config, ValueSerializer<Boolean> serializer, String[] comments, String key, Boolean def) {
        super(config, serializer, comments, key, def);
        reload();
    }

}
