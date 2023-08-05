package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DoubleConfigEntry extends RangedConfigEntryImpl<Double> {

    public DoubleConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Double def, Double min, Double max) {
        super(config, comments, key, def, min, max);
        reload();
    }

    @Nonnull
    @Override
    Double minimumPossibleValue() {
        return Double.MIN_VALUE;
    }

    @Nonnull
    @Override
    Double maximumPossibleValue() {
        return Double.MAX_VALUE;
    }

    @Override
    @Nullable
    Double deserialize(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    Double fixValue(Double value) {
        return Math.max(Math.min(value, max), min);
    }

    @Override
    String serialize(Double val) {
        return String.valueOf(val);
    }
}
