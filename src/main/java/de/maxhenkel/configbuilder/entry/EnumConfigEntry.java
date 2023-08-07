package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

public class EnumConfigEntry<E extends Enum<E>> extends AbstractConfigEntry<E> {

    public EnumConfigEntry(CommentedPropertyConfig config, ValueSerializer<E> serializer, String[] comments, String key, E def) {
        super(config, serializer, comments, key, def);
        reload();
    }

}
