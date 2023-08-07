package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractRangedConfigEntry<T> extends AbstractConfigEntry<T> implements RangedConfigEntry<T> {

    @Nonnull
    protected final T min;
    @Nonnull
    protected final T max;

    public AbstractRangedConfigEntry(CommentedPropertyConfig config, ValueSerializer<T> serializer, String[] comments, String key, T def, @Nullable T min, @Nullable T max) {
        super(config, serializer, comments, key, def);
        if (min != null) {
            this.min = min;
        } else {
            this.min = minimumPossibleValue();
        }
        if (max != null) {
            this.max = max;
        } else {
            this.max = maximumPossibleValue();
        }
    }

    @Nonnull
    @Override
    public T getMin() {
        return min;
    }

    @Nonnull
    @Override
    public T getMax() {
        return max;
    }

    /**
     * @return the minimum possible value the data type can have
     */
    @Nonnull
    abstract T minimumPossibleValue();

    /**
     * @return the maximum possible value the data type can have
     */
    @Nonnull
    abstract T maximumPossibleValue();

}
