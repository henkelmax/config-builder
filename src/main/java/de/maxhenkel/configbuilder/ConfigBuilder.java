package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigBuilder {

    protected Config config;
    protected List<ConfigEntry<?>> entries;

    private ConfigBuilder(Config config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    static ConfigBuilder createInternal(Path path) {
        return new ConfigBuilder(new Config(path));
    }

    public static void create(Path path, Consumer<ConfigBuilder> builderConsumer) {
        ConfigBuilder builder = createInternal(path);
        builderConsumer.accept(builder);
        builder.config.save();
    }

    void reloadFromDisk() {
        config.reload();
        entries.forEach(ConfigEntry::loadOrDefault);
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

    public <T extends Enum> ConfigEntry<T> enumEntry(String key, T def) {
        EnumConfigEntry<T> entry = new EnumConfigEntry(config, def.getClass());
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return (ConfigEntry<T>) entry;
    }

    public static abstract class ConfigEntry<T> {
        protected Config config;
        protected String key;
        protected T value, def;

        private ConfigEntry(Config config) {
            this.config = config;
        }

        public T get() {
            return value;
        }

        public ConfigEntry<T> set(T value) {
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

        public ConfigEntry<T> reset() {
            value = def;
            config.getProperties().setProperty(key, serialize(def));
            return this;
        }

        public ConfigEntry<T> save() {
            config.save();
            return this;
        }

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

        public Config getConfig() {
            return config;
        }
    }

    private static class BooleanConfigEntry extends ConfigEntry<Boolean> {

        private BooleanConfigEntry(Config config) {
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

    private static class IntegerConfigEntry extends ConfigEntry<Integer> {

        private final int min, max;

        public IntegerConfigEntry(Config config, int min, int max) {
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

    private static class DoubleConfigEntry extends ConfigEntry<Double> {

        private final double min, max;

        public DoubleConfigEntry(Config config, double min, double max) {
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

    private static class StringConfigEntry extends ConfigEntry<String> {

        private StringConfigEntry(Config config) {
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

    private static class EnumConfigEntry<T extends Enum> extends ConfigEntry<Enum> {
        protected Class<T> enumClass;

        public EnumConfigEntry(Config config, Class<T> enumClass) {
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

    private static class IntegerListConfigEntry extends ConfigEntry<List<Integer>> {

        private IntegerListConfigEntry(Config config) {
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
