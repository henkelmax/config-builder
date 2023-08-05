package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LongConfigEntry extends RangedConfigEntryImpl<Long> {

    public LongConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Long def, Long min, Long max) {
        super(config, comments, key, def, min, max);
        reload();
    }

    @Override
    @Nullable
    public Long deserialize(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    @Override
    protected Long minimumPossibleValue() {
        return Long.MIN_VALUE;
    }

    @Nonnull
    @Override
    protected Long maximumPossibleValue() {
        return Long.MAX_VALUE;
    }

    @Override
    protected Long fixValue(Long value) {
        return Math.max(Math.min(value, max), min);
    }

    @Override
    public String serialize(Long val) {
        return String.valueOf(val);
    }
}
