# Config Builder

A simple Java configuration library.

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
    Config config = ConfigBuilder.builder(Config::new).path(Paths.get("config.properties")).keepOrder(true).removeUnused(true).strict(true).build();

    System.out.println(config.booleanEntry.getKey() +": " +config.booleanEntry.get());  // boolean: false
    System.out.println(config.integerEntry.getKey() +": " +config.integerEntry.get());  // integer: 10
    System.out.println(config.longEntry.getKey() +": " +config.longEntry.get());        // long: 10
    System.out.println(config.doubleEntry.getKey() +": " +config.doubleEntry.get());    // double: 10.0
    System.out.println(config.stringEntry.getKey() +": " +config.stringEntry.get());    // string: test123
    System.out.println(config.enumEntry.getKey() +": " +config.enumEntry.get());        // enum: TEST_1
}

class Config {
    public final ConfigEntry<Boolean> booleanEntry;
    public final ConfigEntry<Integer> integerEntry;
    public final ConfigEntry<Long> longEntry;
    public final ConfigEntry<Double> doubleEntry;
    public final ConfigEntry<String> stringEntry;
    public final ConfigEntry<TestEnum> enumEntry;

    public Config(ConfigBuilder builder) {
        booleanEntry = builder.booleanEntry("boolean", false);
        integerEntry = builder.integerEntry("integer", 10, 0, 20);
        longEntry = builder.longEntry("long", 10L, 0L, 20L);
        doubleEntry = builder.doubleEntry("double", 10D, 0D, 20D);
        stringEntry = builder.stringEntry("string", "test123");
        enumEntry = builder.enumEntry("enum", TestEnum.TEST_1);
    }
}

enum TestEnum {
    TEST_1, TEST_2, TEST_3;
}
```