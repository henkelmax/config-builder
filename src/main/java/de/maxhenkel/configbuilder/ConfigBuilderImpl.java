package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.*;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigBuilderImpl implements ConfigBuilder {

    protected CommentedPropertyConfig config;
    protected List<AbstractConfigEntry<?>> entries;
    protected boolean frozen;

    public ConfigBuilderImpl(CommentedPropertyConfig config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    void removeUnused() {
        List<String> existingKeys = entries.stream().map(AbstractConfigEntry::getKey).collect(Collectors.toList());
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
        entries.forEach(AbstractConfigEntry::reload);
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
    public BooleanConfigEntry booleanEntry(String key, Boolean def, String... comments) {
        checkFrozen();
        BooleanConfigEntry entry = new BooleanConfigEntry(config, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public IntegerConfigEntry integerEntry(String key, Integer def, Integer min, Integer max, String... comments) {
        checkFrozen();
        IntegerConfigEntry entry = new IntegerConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public LongConfigEntry longEntry(String key, Long def, Long min, Long max, String... comments) {
        checkFrozen();
        LongConfigEntry entry = new LongConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public DoubleConfigEntry doubleEntry(String key, Double def, Double min, Double max, String... comments) {
        checkFrozen();
        DoubleConfigEntry entry = new DoubleConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public FloatConfigEntry floatEntry(String key, Float def, Float min, Float max, String... comments) {
        checkFrozen();
        FloatConfigEntry entry = new FloatConfigEntry(config, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public StringConfigEntry stringEntry(String key, String def, String... comments) {
        checkFrozen();
        StringConfigEntry entry = new StringConfigEntry(config, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public <E extends Enum<E>> EnumConfigEntry<E> enumEntry(String key, E def, String... comments) {
        checkFrozen();
        EnumConfigEntry<E> entry = new EnumConfigEntry(config, comments, key, def, def.getClass());
        entries.add(entry);
        return entry;
    }

    @Override
    public <T> ConfigEntry<T> entry(String key, T def, String... comments) {
        ConfigEntry<T> builtin = getBuiltin(key, def, comments);
        if (builtin != null) {
            return builtin;
        }
        try {
            EntrySerializable annotation = def.getClass().getDeclaredAnnotation(EntrySerializable.class);
            if (annotation == null) {
                throw new IllegalArgumentException(String.format("Unsupported data type: %s", def.getClass().getName()));
            }
            Class<? extends ValueSerializer<?>> entryConverterClass = annotation.value();
            Constructor<? extends ValueSerializer<?>> constructor = entryConverterClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();

            ValueSerializer<T> converter = (ValueSerializer<T>) constructor.newInstance();
            return new GenericConfigEntry<>(config, comments, key, def, converter);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not instantiate entry serializer", e);
        }
    }

    @Nullable
    private <T> ConfigEntry<T> getBuiltin(String key, T def, String... comments) {
        Class<T> clazz = (Class<T>) def.getClass();
        if (Boolean.class.equals(clazz)) {
            return (ConfigEntry<T>) new BooleanConfigEntry(config, comments, key, (Boolean) def);
        } else if (Integer.class.equals(clazz)) {
            return (ConfigEntry<T>) new IntegerConfigEntry(config, comments, key, (Integer) def, null, null);
        } else if (Long.class.equals(clazz)) {
            return (ConfigEntry<T>) new LongConfigEntry(config, comments, key, (Long) def, null, null);
        } else if (Float.class.equals(clazz)) {
            return (ConfigEntry<T>) new FloatConfigEntry(config, comments, key, (Float) def, null, null);
        } else if (Double.class.equals(clazz)) {
            return (ConfigEntry<T>) new DoubleConfigEntry(config, comments, key, (Double) def, null, null);
        } else if (String.class.equals(clazz)) {
            return (ConfigEntry<T>) new StringConfigEntry(config, comments, key, (String) def);
        } else if (Enum.class.isAssignableFrom(clazz)) {
            return (ConfigEntry<T>) new EnumConfigEntry(config, comments, key, (Enum) def, clazz);
        }
        return null;
    }

}
