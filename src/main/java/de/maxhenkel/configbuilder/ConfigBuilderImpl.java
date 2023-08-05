package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.*;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigBuilderImpl implements ConfigBuilder {

    protected CommentedPropertyConfig config;
    protected List<ConfigEntryImpl<?>> entries;
    protected boolean frozen;

    public ConfigBuilderImpl(CommentedPropertyConfig config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    void removeUnused() {
        List<String> existingKeys = entries.stream().map(ConfigEntryImpl::getKey).collect(Collectors.toList());
        List<String> toRemove = config.getProperties().keySet().stream().filter(s -> !existingKeys.contains(s)).collect(Collectors.toList());
        for (String key : toRemove) {
            config.getProperties().remove(key);
        }
    }

    void sortEntries() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < entries.size(); i++) {
            map.put(entries.get(i).getKey(), i);
        }
        config.getProperties().sort(Comparator.comparingInt(o -> map.getOrDefault(o, Integer.MAX_VALUE)));
    }

    void reloadFromDisk() {
        config.reload();
        entries.forEach(ConfigEntryImpl::reload);
    }

    void freeze() {
        frozen = true;
    }

    void checkFrozen() {
        if (frozen) {
            throw new IllegalStateException("ConfigBuilder is frozen");
        }
    }

    @Override
    public ConfigBuilderImpl header(String... header) {
        checkFrozen();
        config.getProperties().setHeaderComments(Arrays.asList(header));
        return this;
    }

    @Override
    public ConfigEntry<Boolean> booleanEntry(String key, boolean def, String... comments) {
        checkFrozen();
        BooleanConfigEntry entry = new BooleanConfigEntry(config, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Integer> integerEntry(String key, int def, int min, int max, String... comments) {
        checkFrozen();
        IntegerConfigEntry entry = new IntegerConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Long> longEntry(String key, long def, long min, long max, String... comments) {
        checkFrozen();
        LongConfigEntry entry = new LongConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Double> doubleEntry(String key, double def, double min, double max, String... comments) {
        checkFrozen();
        DoubleConfigEntry entry = new DoubleConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<String> stringEntry(String key, String def, String... comments) {
        checkFrozen();
        StringConfigEntry entry = new StringConfigEntry(config, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<List<Integer>> integerListEntry(String key, List<Integer> def, String... comments) {
        checkFrozen();
        IntegerListConfigEntry entry = new IntegerListConfigEntry(config, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> enumEntry(String key, T def, String... comments) {
        checkFrozen();
        EnumConfigEntry<T> entry = new EnumConfigEntry(config, comments, key, def, def.getClass());
        entries.add(entry);
        return (ConfigEntryImpl<T>) entry;
    }

}
