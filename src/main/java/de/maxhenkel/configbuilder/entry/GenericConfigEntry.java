package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

public class GenericConfigEntry<T> extends AbstractConfigEntry<T> {

    public GenericConfigEntry(CommentedPropertyConfig config, ValueSerializer<T> serializer, String[] comments, String key, T def) {
        super(config, serializer, comments, key, def);
        reload();
    }

}
