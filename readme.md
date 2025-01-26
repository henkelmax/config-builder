# ConfigBuilder

A simple Java property configuration library.

## Features

- Simple API
- Supports boolean, integer, long, float, double, string and enum entries out of the box
- Supports custom entry types
- Uses the simple, human-readable [properties](https://en.wikipedia.org/wiki/.properties) file format
- Automatically handles saving and loading
- Automatically corrects invalid values or missing entries
- Supports header and per-entry comments
- Supports default values
- Supports min and max values
- Zero dependencies
- Supports Java 8 and above
- Extensively tested with unit tests (100% code coverage)

## Usage

**Maven**

``` xml
<dependency>
  <groupId>de.maxhenkel.configbuilder</groupId>
  <artifactId>configbuilder</artifactId>
  <version>2.0.2</version>
</dependency>

<repositories>
  <repository>
    <id>henkelmax.public</id>
    <url>https://maven.maxhenkel.de/repository/public</url>
  </repository>
</repositories>
```

**Gradle**

``` groovy
dependencies {
  implementation 'de.maxhenkel.configbuilder:configbuilder:2.0.2'
}

repositories {
  maven {
    name = 'henkelmax.public'
    url = 'https://maven.maxhenkel.de/repository/public'
  }
}
```

## Example Code

```java
public static void main(String[] args) {
    Config config = ConfigBuilder.builder(Config::new)
        .path(Paths.get("config.properties")) // The path to the config file
        .keepOrder(true) // Whether the config should keep the order of the entries - Enabled by default
        .removeUnused(true) // Whether the config should remove entries that were not defined in the builder - Enabled by default
        .strict(true) // Whether the config should be strict (compliant to Javas Properties implementation) - Disabled by default
        .saveAfterBuild(true) // Whether the config should be saved after building - Enabled by default
        .build();

    System.out.println(config.booleanEntry.getKey() + ": " + config.booleanEntry.get());  // boolean: false
    System.out.println(config.integerEntry.getKey() + ": " + config.integerEntry.get());  // integer: 10
    System.out.println(config.longEntry.getKey() + ": " + config.longEntry.get());        // long: 10
    System.out.println(config.floatEntry.getKey() + ": " + config.floatEntry.get());      // float: 10.0
    System.out.println(config.doubleEntry.getKey() + ": " + config.doubleEntry.get());    // double: 10.0
    System.out.println(config.stringEntry.getKey() + ": " + config.stringEntry.get());    // string: test123
    System.out.println(config.enumEntry.getKey() + ": " + config.enumEntry.get());        // enum: TEST_1

    config.integerEntry.set(15).save(); // Set the value of the integer entry to 15 and saves the config asynchronously
    config.integerEntry.set(12).saveSync(); // Set the value of the integer entry to 12 and saves the config synchronously
    config.integerEntry.reset().save(); // Reset the value of the integer entry to the default value and saves the config asynchronously
}

public class Config {
    public final ConfigEntry<Boolean> booleanEntry;
    public final ConfigEntry<Integer> integerEntry;
    public final ConfigEntry<Long> longEntry;
    public final ConfigEntry<Float> floatEntry;
    public final ConfigEntry<Double> doubleEntry;
    public final ConfigEntry<String> stringEntry;
    public final ConfigEntry<TestEnum> enumEntry;

    public Config(ConfigBuilder builder) {
        builder.header(
                "My config version 1.0.0",
                "This is a test config"
        );
        booleanEntry = builder.booleanEntry("boolean", false).comment("This is a boolean entry");
        integerEntry = builder.integerEntry("integer", 10, 0, 20).comment("This is an integer entry with the range 0-20");
        longEntry = builder.longEntry("long", 10L, 0L, 20L).comment("This is a long entry with the range 0-20");
        floatEntry = builder.floatEntry("float", 10F, 0F, 20F).comment("This is a float entry with the range 0.0-20.0");
        doubleEntry = builder.doubleEntry("double", 10D, 0D, 20D).comment("This is a double entry with the range 0.0-20.0");
        stringEntry = builder.stringEntry("string", "test123").comment("This is a string entry");
        enumEntry = builder.enumEntry("enum", TestEnum.TEST_1).comment("This is an enum entry");
    }
}

public enum TestEnum {
    TEST_1, TEST_2, TEST_3;
}
```

Generated config file:

```properties
# My config version 1.0.0
# This is a test config

# This is a boolean entry
boolean=false
# This is an integer entry with the range 0-20
integer=10
# This is a long entry with the range 0-20
long=10
# This is a float entry with the range 0.0-20.0
float=10.0
# This is a double entry with the range 0.0-20.0
double=10.0
# This is a string entry
string=test123
# This is an enum entry
enum=TEST_1
```

## Supported Value Types

Besides the types shown in the above example, there are several built-in types that are supported out of the box.

### UUIDs

```java
public class Config {
    public final ConfigEntry<UUID> uuidEntry;
    public Config(ConfigBuilder builder) {
        uuidEntry = builder.entry("uuid_entry", UUID.fromString("cee9f733-870b-48e3-936f-d3be5d527524")).comment("This is a UUID entry");
    }
}
```

### Lists

The `StringList` type is a wrapper around an immutable list.
It is serialized as a comma separated list of strings.

It can be instantiated with `StringList.of(String...)` or `StringList.of(List<String>)`.

```java
public class Config {
    public final ConfigEntry<StringList> stringListEntry;
    public Config(ConfigBuilder builder) {
        stringListEntry = builder.entry("string_list_entry", StringList.of("test", "test1", "test2")).comment("This is a string list entry");
    }
}
```

The same can be done with `IntegerList`.


### Maps

The `StringMap` type is a wrapper around an immutable map.

It can be instantiated with `StringMap.of(List<Map<String, String>)` or `StringMap.builder().put(key, value).putAll(map).build()`.

```java
public class Config {
    public final ConfigEntry<StringMap> stringMapEntry;
    public Config(ConfigBuilder builder) {
        stringMapEntry = builder.entry("string_map_entry", StringMap.builder().put("test", "123").put("test1", "456").build()).comment("This is a string map entry");
    }
}
```


## Adding Custom Value Types

## Variant 1

Add your value serializer while creating your config builder.
You can also override the default value serializer for an already existing type
by adding a new value serializer for that type.
Custom types should always be immutable.

```java
public static void main(String[] args) {
    ConfigBuilder
            .builder(Config::new)
            .addValueSerializer(CustomType.class, CustomTypeValueSerializer.INSTANCE)
            .build();
}

public class Config {
    public final ConfigEntry<CustomType> customType;

    public Config(ConfigBuilder builder) {
        customType = builder.entry("test", new CustomType("Test")).comment("This is a custom entry");
    }
}

public class CustomType {
    private final String value;

    public CustomType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

public class CustomTypeValueSerializer implements ValueSerializer<CustomType> {
    public static final CustomTypeValueSerializer INSTANCE = new CustomTypeValueSerializer();

    @Nullable
    @Override
    public CustomType deserialize(String str) {
        return new CustomType(str);
    }

    @Nullable
    @Override
    public String serialize(CustomType val) {
        return val.getValue();
    }
}
```

## Variant 2

Annotate your custom type with `@ValueSerializable` and provide a serializer class that implements `ValueSerializer`.
Custom types should always be immutable.

```java
@ValueSerializable(CustomTypeValueSerializer.class)
public class CustomType {
    private final String value;

    public CustomType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
```

Your serializer class should have a no-args constructor and implement `ValueSerializer`.
Invalid conversions should return `null`.

```java
public class CustomTypeValueSerializer implements ValueSerializer<CustomType> {
    @Nullable
    @Override
    public CustomType deserialize(String str) {
        return new CustomType(str);
    }
    
    @Nullable
    @Override
    public String serialize(CustomType val) {
        return val.getValue();
    }
}
```

Custom types can be used like this:

```java
public static void main(String[] args) {
    Config config = ConfigBuilder.builder(Config::new)
            .path(Paths.get("config.properties"))
            .build();

    System.out.println(config.customType.get().getValue()); // Prints "Test"
    config.customType.set(new CustomType("Test2"));
    System.out.println(config.customType.get().getValue()); // Prints "Test2"
}

public class Config {
    public final ConfigEntry<CustomType> customType;

    public Config(ConfigBuilder builder) {
        customType = builder.entry("test", new CustomType("Test")).comment("This is a custom entry");
    }
}
```
