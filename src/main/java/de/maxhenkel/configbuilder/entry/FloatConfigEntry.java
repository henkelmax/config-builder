package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloatConfigEntry extends RangedConfigEntryImpl<Float> {

    public FloatConfigEntry(CommentedPropertyConfig config, String[] comments, String key, Float def, Float min, Float max) {
        super(config, comments, key, def, min, max);
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
    @Nullable
    Float deserialize(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    Float fixValue(Float value) {
        return Math.max(Math.min(value, max), min);
    }

    @Override
    String serialize(Float val) {
        return String.valueOf(val);
    }
}
