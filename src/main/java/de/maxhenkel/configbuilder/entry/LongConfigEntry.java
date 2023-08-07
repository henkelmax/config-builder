package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LongConfigEntry extends AbstractRangedConfigEntry<Long> {

    public LongConfigEntry(CommentedPropertyConfig config, ValueSerializer<Long> serializer, String[] comments, String key, Long def, @Nullable Long min, @Nullable Long max) {
        super(config, serializer, comments, key, def, min, max);
        reload();
    }

    @Nonnull
    @Override
    Long minimumPossibleValue() {
        return Long.MIN_VALUE;
    }

    @Nonnull
    @Override
    Long maximumPossibleValue() {
        return Long.MAX_VALUE;
    }

    @Override
    Long fixValue(Long value) {
        return Math.max(Math.min(value, max), min);
    }

}
