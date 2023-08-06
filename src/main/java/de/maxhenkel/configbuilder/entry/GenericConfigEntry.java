package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nullable;

public class GenericConfigEntry<T> extends AbstractConfigEntry<T> {

    protected final ValueSerializer<T> entrySerializer;

    public GenericConfigEntry(CommentedPropertyConfig config, String[] comments, String key, T def, ValueSerializer<T> entrySerializer) {
        super(config, comments, key, def);
        this.entrySerializer = entrySerializer;
        reload();
    }

    @Nullable
    @Override
    public T deserialize(String str) {
        return entrySerializer.deserialize(str);
    }

    @Nullable
    @Override
    public String serialize(T val) {
        return entrySerializer.serialize(val);
    }

    public ValueSerializer<T> getEntrySerializer() {
        return entrySerializer;
    }
}
