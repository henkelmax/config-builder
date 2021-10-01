package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigBuilder {

    protected PropertyConfig config;
    protected List<ConfigEntryImpl<?>> entries;

    private ConfigBuilder(PropertyConfig config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    static ConfigBuilder buildInternal(Path path) {
        return new ConfigBuilder(new PropertyConfig(path));
    }

    public static <T> T build(Path path, Function<ConfigBuilder, T> builderConsumer) {
        ConfigBuilder builder = buildInternal(path);
        T config = builderConsumer.apply(builder);
        builder.config.save();
        return config;
    }

    void reloadFromDisk() {
        config.reload();
        entries.forEach(ConfigEntryImpl::loadOrDefault);
    }

    public ConfigEntry<Boolean> booleanEntry(String key, boolean def) {
        BooleanConfigEntry entry = new BooleanConfigEntry(config);
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    public ConfigEntry<Integer> integerEntry(String key, int def, int min, int max) {
        IntegerConfigEntry entry = new IntegerConfigEntry(config, min, max);
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    public ConfigEntry<Double> doubleEntry(String key, double def, double min, double max) {
        DoubleConfigEntry entry = new DoubleConfigEntry(config, min, max);
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    public ConfigEntry<String> stringEntry(String key, String def) {
        StringConfigEntry entry = new StringConfigEntry(config);
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    public ConfigEntry<List<Integer>> integerListEntry(String key, List<Integer> def) {
        IntegerListConfigEntry entry = new IntegerListConfigEntry(config);
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    public <T extends Enum<T>> ConfigEntry<T> enumEntry(String key, T def) {
        EnumConfigEntry<T> entry = new EnumConfigEntry(config, def.getClass());
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return (ConfigEntryImpl<T>) entry;
    }

    private static abstract class ConfigEntryImpl<T> implements ConfigEntry<T> {
        protected PropertyConfig config;
        protected String key;
        protected T value, def;

        private ConfigEntryImpl(PropertyConfig config) {
            this.config = config;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public ConfigEntryImpl<T> set(T value) {
            if (this.value.equals(value)) {
                return this;
            }
            this.value = fixValue(value);
            String serialized = serialize(this.value);
            config.getProperties().setProperty(key, serialized);
            return this;
        }

        protected void loadOrDefault() {
            if (config.getProperties().containsKey(key)) {
                T val = deserialize(config.getProperties().getProperty(key));
                if (val == null) {
                    reset();
                } else {
                    value = fixValue(val);
                }
            } else {
                reset();
            }
        }

        @Override
        public ConfigEntry<T> reset() {
            value = def;
            config.getProperties().setProperty(key, serialize(def));
            return this;
        }

        @Override
        public ConfigEntry<T> save() {
            config.save();
            return this;
        }

        @Override
        public ConfigEntry<T> saveSync() {
            config.saveSync();
            return this;
        }

        @Nullable
        public abstract T deserialize(String str);

        public abstract String serialize(T val);

        protected T fixValue(T value) {
            return value;
        }

        @Override
        public Config getConfig() {
            return config;
        }
    }

    private static class BooleanConfigEntry extends ConfigEntryImpl<Boolean> {

        private BooleanConfigEntry(PropertyConfig config) {
            super(config);
        }

        @Override
        @Nullable
        public Boolean deserialize(String str) {
            return Boolean.valueOf(str);
        }

        @Override
        public String serialize(Boolean val) {
            return String.valueOf(val);
        }
    }

    private static class IntegerConfigEntry extends ConfigEntryImpl<Integer> {

        private final int min, max;

        public IntegerConfigEntry(PropertyConfig config, int min, int max) {
            super(config);
            this.min = min;
            this.max = max;
        }

        @Override
        @Nullable
        public Integer deserialize(String str) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        protected Integer fixValue(Integer value) {
            return Math.max(Math.min(value, max), min);
        }

        @Override
        public String serialize(Integer val) {
            return String.valueOf(val);
        }
    }

    private static class DoubleConfigEntry extends ConfigEntryImpl<Double> {

        private final double min, max;

        public DoubleConfigEntry(PropertyConfig config, double min, double max) {
            super(config);
            this.min = min;
            this.max = max;
        }

        @Override
        @Nullable
        public Double deserialize(String str) {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        protected Double fixValue(Double value) {
            return Math.max(Math.min(value, max), min);
        }

        @Override
        public String serialize(Double val) {
            return String.valueOf(val);
        }
    }

    private static class StringConfigEntry extends ConfigEntryImpl<String> {

        private StringConfigEntry(PropertyConfig config) {
            super(config);
        }

        @Override
        @Nullable
        public String deserialize(String str) {
            return str;
        }

        @Override
        public String serialize(String val) {
            return val;
        }
    }

    private static class EnumConfigEntry<T extends Enum> extends ConfigEntryImpl<Enum> {
        protected Class<T> enumClass;

        public EnumConfigEntry(PropertyConfig config, Class<T> enumClass) {
            super(config);
            this.enumClass = enumClass;
        }

        @Override
        @Nullable
        public Enum deserialize(String str) {
            try {
                return Enum.valueOf(enumClass, str);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String serialize(Enum val) {
            return val.name();
        }
    }

    private static class IntegerListConfigEntry extends ConfigEntryImpl<List<Integer>> {

        private IntegerListConfigEntry(PropertyConfig config) {
            super(config);
        }

        @Override
        @Nullable
        public List<Integer> deserialize(String str) {
            List<Integer> list = new ArrayList<>();
            String[] split = str.split(",");
            for (String n : split) {
                try {
                    list.add(Integer.parseInt(n));
                } catch (NumberFormatException e) {
                }
            }
            return list;
        }

        @Override
        public String serialize(List<Integer> val) {
            return val.stream().map(String::valueOf).collect(Collectors.joining(","));
        }
    }

}
