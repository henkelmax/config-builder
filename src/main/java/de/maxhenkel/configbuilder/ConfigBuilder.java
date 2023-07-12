package de.maxhenkel.configbuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public interface ConfigBuilder {

    ConfigBuilder header(String... header);

    ConfigEntry<Boolean> booleanEntry(String key, boolean def, String... comments);

    ConfigEntry<Integer> integerEntry(String key, int def, int min, int max, String... comments);

    ConfigEntry<Long> longEntry(String key, long def, long min, long max, String... comments);

    ConfigEntry<Double> doubleEntry(String key, double def, double min, double max, String... comments);

    ConfigEntry<String> stringEntry(String key, String def, String... comments);

    ConfigEntry<List<Integer>> integerListEntry(String key, List<Integer> def, String... comments);

    <E extends Enum<E>> ConfigEntry<E> enumEntry(String key, E def, String... comments);

    static <C> C build(Path path, Function<ConfigBuilder, C> builderConsumer) {
        return build(path, false, builderConsumer);
    }

    static <C> C build(Path path, boolean removeUnused, Function<ConfigBuilder, C> builderConsumer) {
        ConfigBuilderImpl builder = ConfigBuilderImpl.buildInternal(path);
        C config = builderConsumer.apply(builder);
        if (removeUnused) {
            builder.removeUnused();
        }
        builder.sortEntries();
        builder.config.save();
        return config;
    }

}
