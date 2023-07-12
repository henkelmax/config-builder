package de.maxhenkel.configbuilder;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigBuilderImpl implements ConfigBuilder {

    protected CommentedPropertyConfig config;
    protected List<ConfigEntryImpl<?>> entries;

    private ConfigBuilderImpl(CommentedPropertyConfig config) {
        this.config = config;
        this.entries = new ArrayList<>();
    }

    static ConfigBuilderImpl buildInternal(Path path) {
        return new ConfigBuilderImpl(new CommentedPropertyConfig(path));
    }

    void removeUnused() {
        List<String> existingKeys = entries.stream().map(configEntry -> configEntry.key).collect(Collectors.toList());
        List<String> toRemove = config.getProperties().keySet().stream().filter(s -> !existingKeys.contains(s)).collect(Collectors.toList());
        for (String key : toRemove) {
            config.getProperties().remove(key);
        }
    }

    void sortEntries() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < entries.size(); i++) {
            map.put(entries.get(i).key, i);
        }
        config.getProperties().sort(Comparator.comparingInt(o -> map.getOrDefault(o, Integer.MAX_VALUE)));
    }

    void reloadFromDisk() {
        config.reload();
        entries.forEach(ConfigEntryImpl::loadOrDefault);
    }

    @Override
    public ConfigBuilderImpl header(String... header) {
        config.getProperties().setHeaderComments(Arrays.asList(header));
        return this;
    }

    @Override
    public ConfigEntry<Boolean> booleanEntry(String key, boolean def, String... comments) {
        BooleanConfigEntry entry = new BooleanConfigEntry(config);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Integer> integerEntry(String key, int def, int min, int max, String... comments) {
        IntegerConfigEntry entry = new IntegerConfigEntry(config, min, max);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Long> longEntry(String key, long def, long min, long max, String... comments) {
        LongConfigEntry entry = new LongConfigEntry(config, min, max);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<Double> doubleEntry(String key, double def, double min, double max, String... comments) {
        DoubleConfigEntry entry = new DoubleConfigEntry(config, min, max);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<String> stringEntry(String key, String def, String... comments) {
        StringConfigEntry entry = new StringConfigEntry(config);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public ConfigEntry<List<Integer>> integerListEntry(String key, List<Integer> def, String... comments) {
        IntegerListConfigEntry entry = new IntegerListConfigEntry(config);
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return entry;
    }

    @Override
    public <T extends Enum<T>> ConfigEntry<T> enumEntry(String key, T def, String... comments) {
        EnumConfigEntry<T> entry = new EnumConfigEntry(config, def.getClass());
        entry.comments = comments;
        entry.key = key;
        entry.def = def;
        entry.loadOrDefault();
        entries.add(entry);
        return (ConfigEntryImpl<T>) entry;
    }

    public static abstract class ConfigEntryImpl<T> implements ConfigEntry<T> {
        protected CommentedPropertyConfig config;
        protected String[] comments;
        protected String key;
        protected T value, def;

        private ConfigEntryImpl(CommentedPropertyConfig config) {
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
            config.getProperties().set(key, serialized, comments);
            return this;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public ConfigEntryImpl<T> comment(String... comments) {
            this.comments = comments;
            config.getProperties().setComments(key, Arrays.asList(comments));
            return this;
        }

        @Override
        public String[] getComments() {
            return comments;
        }

        protected void loadOrDefault() {
            if (config.getProperties().containsKey(key)) {
                T val = deserialize(config.getProperties().get(key));
                if (val == null) {
                    reset();
                } else {
                    value = fixValue(val);
                    config.properties.setComments(key, Arrays.asList(comments));
                }
            } else {
                reset();
            }
        }

        @Override
        public ConfigEntry<T> reset() {
            value = def;
            config.getProperties().set(key, serialize(def), comments);
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
        public T getDefault() {
            return def;
        }

        @Override
        public Config getConfig() {
            return config;
        }
    }

    public static class BooleanConfigEntry extends ConfigEntryImpl<Boolean> {

        private BooleanConfigEntry(CommentedPropertyConfig config) {
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

    public static class IntegerConfigEntry extends ConfigEntryImpl<Integer> {

        private final int min, max;

        private IntegerConfigEntry(CommentedPropertyConfig config, int min, int max) {
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

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
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

    public static class LongConfigEntry extends ConfigEntryImpl<Long> {

        private final long min, max;

        private LongConfigEntry(CommentedPropertyConfig config, long min, long max) {
            super(config);
            this.min = min;
            this.max = max;
        }

        @Override
        @Nullable
        public Long deserialize(String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public long getMin() {
            return min;
        }

        public long getMax() {
            return max;
        }

        @Override
        protected Long fixValue(Long value) {
            return Math.max(Math.min(value, max), min);
        }

        @Override
        public String serialize(Long val) {
            return String.valueOf(val);
        }
    }

    public static class DoubleConfigEntry extends ConfigEntryImpl<Double> {

        private final double min, max;

        private DoubleConfigEntry(CommentedPropertyConfig config, double min, double max) {
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

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
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

    public static class StringConfigEntry extends ConfigEntryImpl<String> {

        private StringConfigEntry(CommentedPropertyConfig config) {
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

    public static class EnumConfigEntry<T extends Enum> extends ConfigEntryImpl<Enum> {
        protected Class<T> enumClass;

        private EnumConfigEntry(CommentedPropertyConfig config, Class<T> enumClass) {
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

    public static class IntegerListConfigEntry extends ConfigEntryImpl<List<Integer>> {

        private IntegerListConfigEntry(CommentedPropertyConfig config) {
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
