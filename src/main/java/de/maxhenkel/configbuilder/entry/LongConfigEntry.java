package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LongConfigEntry extends RangedConfigEntryImpl<Long> {

    public LongConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Long def, Long min, Long max) {
        super(config, comments, key, def, min, max);
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

    @Nullable
    @Override
    public Long deserialize(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String serialize(Long val) {
        return String.valueOf(val);
    }
}
