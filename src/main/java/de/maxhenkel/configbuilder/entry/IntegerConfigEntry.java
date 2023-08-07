package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntegerConfigEntry extends AbstractRangedConfigEntry<Integer> {

    public IntegerConfigEntry(CommentedPropertyConfig config, ValueSerializer<Integer> serializer, String[] comments, String key, Integer def, @Nullable Integer min, @Nullable Integer max) {
        super(config, serializer, comments, key, def, min, max);
        reload();
    }

    @Nonnull
    @Override
    Integer minimumPossibleValue() {
        return Integer.MIN_VALUE;
    }

    @Nonnull
    @Override
    Integer maximumPossibleValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    Integer fixValue(Integer value) {
        return Math.max(Math.min(value, max), min);
    }

}
