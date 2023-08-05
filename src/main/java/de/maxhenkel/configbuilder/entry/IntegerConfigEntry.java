package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntegerConfigEntry extends RangedConfigEntryImpl<Integer> {

    public IntegerConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Integer def, Integer min, Integer max) {
        super(config, comments, key, def, min, max);
        reload();
    }

    @Nonnull
    @Override
    protected Integer minimumPossibleValue() {
        return Integer.MIN_VALUE;
    }

    @Nonnull
    @Override
    protected Integer maximumPossibleValue() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public Integer deserialize(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected Integer fixValue(Integer value) {
        return Math.max(Math.min(value, max), min);
    }

    @Override
    public String serialize(Integer val) {
        return String.valueOf(val);
    }
}