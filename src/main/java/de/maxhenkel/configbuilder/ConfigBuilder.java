package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public interface ConfigBuilder {

    /**
     * Sets header comments for the config
     * <br/>
     * Calling this method multiple times will overwrite the previous header
     *
     * @param header The header comments
     * @return the config builder
     */
    ConfigBuilder header(String... header);

    /**
     * Adds a string entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    BooleanConfigEntry booleanEntry(String key, Boolean def, String... comments);

    /**
     * Adds an integer entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param min      the minimum value
     * @param max      the maximum value
     * @param comments the comments
     * @return the config entry
     */
    IntegerConfigEntry integerEntry(String key, Integer def, Integer min, Integer max, String... comments);

    /**
     * Adds an integer entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    default IntegerConfigEntry integerEntry(String key, Integer def, String... comments) {
        return integerEntry(key, def, null, null, comments);
    }

    /**
     * Adds a long entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param min      the minimum value
     * @param max      the maximum value
     * @param comments the comments
     * @return the config entry
     */
    LongConfigEntry longEntry(String key, Long def, Long min, Long max, String... comments);

    /**
     * Adds a long entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    default LongConfigEntry longEntry(String key, Long def, String... comments) {
        return longEntry(key, def, null, null, comments);
    }

    /**
     * Adds a double entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param min      the minimum value
     * @param max      the maximum value
     * @param comments the comments
     * @return the config entry
     */
    DoubleConfigEntry doubleEntry(String key, Double def, Double min, Double max, String... comments);

    /**
     * Adds a double entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    default DoubleConfigEntry doubleEntry(String key, Double def, String... comments) {
        return doubleEntry(key, def, null, null, comments);
    }

    /**
     * Adds a string entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    StringConfigEntry stringEntry(String key, String def, String... comments);

    /**
     * Adds an integer list entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @return the config entry
     */
    IntegerListConfigEntry integerListEntry(String key, List<Integer> def, String... comments);

    /**
     * Adds an enum entry
     *
     * @param key      the config key
     * @param def      the default value
     * @param comments the comments
     * @param <E>      the enum type
     * @return the config entry
     */
    <E extends Enum<E>> EnumConfigEntry<E> enumEntry(String key, E def, String... comments);

    /**
     * @param path            the storage location of the config file
     * @param builderConsumer the builder consumer
     * @param <C>             your config class
     * @return the config
     * @deprecated Use {@link #builder(Function)} instead
     */
    @Deprecated
    static <C> C build(Path path, Function<ConfigBuilder, C> builderConsumer) {
        return build(path, false, builderConsumer);
    }

    /**
     * @param path            the storage location of the config file
     * @param removeUnused    whether unused entries should be removed
     * @param builderConsumer the builder consumer
     * @param <C>             your config class
     * @return the config
     * @deprecated Use {@link #builder(Function)} instead
     */
    @Deprecated
    static <C> C build(Path path, boolean removeUnused, Function<ConfigBuilder, C> builderConsumer) {
        return ConfigBuilder.builder(builderConsumer)
                .path(path)
                .removeUnused(removeUnused)
                .build();
    }

    /**
     * Creates a new builder to build a config
     *
     * @param builderConsumer the builder consumer
     * @param <C>             your config class
     * @return the builder
     */
    static <C> Builder<C> builder(@Nonnull Function<ConfigBuilder, C> builderConsumer) {
        return new Builder<>(builderConsumer);
    }

    class Builder<C> {
        @Nonnull
        private final Function<ConfigBuilder, C> builderConsumer;
        @Nullable
        private Path path;
        private boolean removeUnused;
        private boolean strict;
        private boolean keepOrder;
        private boolean saveAfterBuild;

        private Builder(@Nonnull Function<ConfigBuilder, C> builderConsumer) {
            this.builderConsumer = builderConsumer;
            this.removeUnused = false;
            this.strict = true;
            this.keepOrder = true;
            this.saveAfterBuild = true;
        }

        /**
         * If the path is not specified, the config will be in-memory only and not saved anywhere
         *
         * @param path the storage location of the config file
         * @return the builder
         */
        public Builder<C> path(Path path) {
            this.path = path;
            return this;
        }

        /**
         * This value is <code>false</code> by default
         *
         * @param removeUnused whether unused entries should be removed
         * @return the builder
         */
        public Builder<C> removeUnused(boolean removeUnused) {
            this.removeUnused = removeUnused;
            return this;
        }

        /**
         * A strict config is compliant to Javas {@link java.util.Properties} class
         * <br/>
         * This value is <code>true</code> by default
         *
         * @param strict whether the config should be strict
         * @return the builder
         */
        public Builder<C> strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * This will only get applied to the config file if {@link #saveAfterBuild} is true or the config is saved manually
         * <br/>
         * This value is <code>true</code> by default
         *
         * @param keepOrder whether the config should keep the order of the entries
         * @return the builder
         */
        public Builder<C> keepOrder(boolean keepOrder) {
            this.keepOrder = keepOrder;
            return this;
        }

        /**
         * This value is <code>true</code> by default
         *
         * @param saveAfterBuild whether the config should be saved after building
         * @return the builder
         */
        public Builder<C> saveAfterBuild(boolean saveAfterBuild) {
            this.saveAfterBuild = saveAfterBuild;
            return this;
        }

        /**
         * Builds the config
         *
         * @return the config
         * @throws IllegalStateException if {@link #path} was not set
         */
        public C build() {
            CommentedPropertyConfig cpc = CommentedPropertyConfig.builder().path(path).strict(strict).build();

            ConfigBuilderImpl builder = new ConfigBuilderImpl(cpc);
            C config = builderConsumer.apply(builder);
            builder.freeze();
            if (removeUnused) {
                builder.removeUnused();
            }
            if (keepOrder) {
                builder.sortEntries();
            }
            if (saveAfterBuild) {
                builder.config.save();
            }
            return config;
        }

    }

}
