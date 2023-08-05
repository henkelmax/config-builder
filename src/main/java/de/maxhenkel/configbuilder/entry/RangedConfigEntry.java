package de.maxhenkel.configbuilder.entry;

import javax.annotation.Nonnull;

public interface RangedConfigEntry<T> extends ConfigEntry<T> {

    /**
     * @return the minimum value the config entry can have
     */
    @Nonnull
    T getMin();

    /**
     * @return the maximum value the config entry can have
     */
    @Nonnull
    T getMax();

}
