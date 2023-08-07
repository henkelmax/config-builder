package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloatConfigEntry extends AbstractRangedConfigEntry<Float> {

    public FloatConfigEntry(CommentedPropertyConfig config, ValueSerializer<Float> serializer, String[] comments, String key, Float def, @Nullable Float min, @Nullable Float max) {
        super(config, serializer, comments, key, def, min, max);
        reload();
    }

    @Nonnull
    @Override
    Float minimumPossibleValue() {
        return Float.MIN_VALUE;
    }

    @Nonnull
    @Override
    Float maximumPossibleValue() {
        return Float.MAX_VALUE;
    }

    @Override
    Float fixValue(Float value) {
        return Math.max(Math.min(value, max), min);
    }

}
