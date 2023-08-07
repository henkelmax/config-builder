package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.custom.StringList;
import de.maxhenkel.configbuilder.custom.serializer.StringListValueSerializer;
import de.maxhenkel.configbuilder.custom.serializer.UUIDSerializer;
import de.maxhenkel.configbuilder.entry.*;
import de.maxhenkel.configbuilder.entry.serializer.*;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigBuilderImpl implements ConfigBuilder {

    protected final CommentedPropertyConfig config;
    protected final Map<Class<?>, ValueSerializer<?>> valueSerializers;
    protected List<AbstractConfigEntry<?>> entries;
    protected boolean frozen;

    public ConfigBuilderImpl(CommentedPropertyConfig config) {
        this(config, null);
    }

    public ConfigBuilderImpl(CommentedPropertyConfig config, @Nullable Map<Class<?>, ValueSerializer<?>> customValueSerializers) {
        this.config = config;
        this.valueSerializers = getDefaultValueSerializers();
        if (customValueSerializers != null) {
            this.valueSerializers.putAll(customValueSerializers);
        }
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
        BooleanConfigEntry entry = new BooleanConfigEntry(config, BooleanSerializer.INSTANCE, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public IntegerConfigEntry integerEntry(String key, Integer def, Integer min, Integer max, String... comments) {
        checkFrozen();
        IntegerConfigEntry entry = new IntegerConfigEntry(config, IntegerSerializer.INSTANCE, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public LongConfigEntry longEntry(String key, Long def, Long min, Long max, String... comments) {
        checkFrozen();
        LongConfigEntry entry = new LongConfigEntry(config, LongSerializer.INSTANCE, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public DoubleConfigEntry doubleEntry(String key, Double def, Double min, Double max, String... comments) {
        checkFrozen();
        DoubleConfigEntry entry = new DoubleConfigEntry(config, DoubleSerializer.INSTANCE, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public FloatConfigEntry floatEntry(String key, Float def, Float min, Float max, String... comments) {
        checkFrozen();
        FloatConfigEntry entry = new FloatConfigEntry(config, FloatSerializer.INSTANCE, comments, key, def, min, max);
        entries.add(entry);
        return entry;
    }

    @Override
    public StringConfigEntry stringEntry(String key, String def, String... comments) {
        checkFrozen();
        StringConfigEntry entry = new StringConfigEntry(config, StringSerializer.INSTANCE, comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public <E extends Enum<E>> EnumConfigEntry<E> enumEntry(String key, E def, String... comments) {
        checkFrozen();
        EnumConfigEntry<E> entry = new EnumConfigEntry<>(config, new EnumSerializer<>((Class<E>) def.getClass()), comments, key, def);
        entries.add(entry);
        return entry;
    }

    @Override
    public <T> ConfigEntry<T> entry(String key, T def, String... comments) {
        checkFrozen();
        ValueSerializer<?> valueSerializer = valueSerializers.get(def.getClass());
        if (valueSerializer != null) {
            return new GenericConfigEntry<>(config, (ValueSerializer<T>) valueSerializer, comments, key, def);
        }
        if (def instanceof Enum<?>) {
            return enumEntry(key, (Enum) def, comments);
        }
        try {
            ValueSerializable annotation = def.getClass().getDeclaredAnnotation(ValueSerializable.class);
            if (annotation == null) {
                throw new IllegalArgumentException(String.format("Unsupported data type: %s", def.getClass().getName()));
            }
            Class<? extends ValueSerializer<?>> entryConverterClass = annotation.value();
            Constructor<? extends ValueSerializer<?>> constructor = entryConverterClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();

            ValueSerializer<T> converter = (ValueSerializer<T>) constructor.newInstance();
            return new GenericConfigEntry<>(config, converter, comments, key, def);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not instantiate value serializer", e);
        }
    }

    protected static Map<Class<?>, ValueSerializer<?>> getDefaultValueSerializers() {
        Map<Class<?>, ValueSerializer<?>> valueSerializers = new HashMap<>();

        // Primitive types
        valueSerializers.put(Boolean.class, new BooleanSerializer());
        valueSerializers.put(Integer.class, new IntegerSerializer());
        valueSerializers.put(Long.class, new LongSerializer());
        valueSerializers.put(Float.class, new FloatSerializer());
        valueSerializers.put(Double.class, new DoubleSerializer());
        valueSerializers.put(String.class, new StringSerializer());

        // Builtin types
        valueSerializers.put(UUID.class, UUIDSerializer.INSTANCE);
        valueSerializers.put(StringList.class, StringListValueSerializer.INSTANCE);

        return valueSerializers;
    }

}
