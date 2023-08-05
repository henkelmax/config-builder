# ConfigBuilder

A simple Java property configuration library.

## Features

- Simple API
- Supports boolean, integer, long, double, string and enum entries
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
  <version>1.2.0</version>
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
  implementation 'de.maxhenkel.configbuilder:configbuilder:1.2.0'
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
        .strict(true) // Whether the config should be strict (compliant to Javas Properties implementation) - Enabled by default
        .saveAfterBuild(true) // Whether the config should be saved after building - Enabled by default
        .build();

    System.out.println(config.booleanEntry.getKey() + ": " + config.booleanEntry.get());  // boolean: false
    System.out.println(config.integerEntry.getKey() + ": " + config.integerEntry.get());  // integer: 10
    System.out.println(config.longEntry.getKey() + ": " + config.longEntry.get());        // long: 10
    System.out.println(config.doubleEntry.getKey() + ": " + config.doubleEntry.get());    // double: 10.0
    System.out.println(config.stringEntry.getKey() + ": " + config.stringEntry.get());    // string: test123
    System.out.println(config.enumEntry.getKey() + ": " + config.enumEntry.get());        // enum: TEST_1

    config.integerEntry.set(15).save(); // Set the value of the integer entry to 15 and saves the config asynchronously
    config.integerEntry.set(12).saveSync(); // Set the value of the integer entry to 12 and saves the config synchronously
    config.integerEntry.reset().save(); // Reset the value of the integer entry to the default value and saves the config asynchronously
}

static class Config {
    public final ConfigEntry<Boolean> booleanEntry;
    public final ConfigEntry<Integer> integerEntry;
    public final ConfigEntry<Long> longEntry;
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
        doubleEntry = builder.doubleEntry("double", 10D, 0D, 20D).comment("This is a double entry with the range 0.0-20.0");
        stringEntry = builder.stringEntry("string", "test123").comment("This is a string entry");
        enumEntry = builder.enumEntry("enum", TestEnum.TEST_1).comment("This is an enum entry");
    }
}

enum TestEnum {
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
# This is a double entry with the range 0.0-20.0
double=10.0
# This is a string entry
string=test123
# This is an enum entry
enum=TEST_1
```