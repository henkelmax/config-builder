package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DoubleConfigEntry extends AbstractRangedConfigEntry<Double> {

    public DoubleConfigEntry(CommentedPropertyConfig config, ValueSerializer<Double> serializer, String[] comments, String key, Double def, @Nullable Double min, @Nullable Double max) {
        super(config, serializer, comments, key, def, min, max);
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
    Double fixValue(Double value) {
        return Math.max(Math.min(value, max), min);
    }

}
