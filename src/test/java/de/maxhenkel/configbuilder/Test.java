package de.maxhenkel.configbuilder;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Test {

    public static ConfigBuilder.ConfigEntry<Boolean> aBooleanEntry;
    public static ConfigBuilder.ConfigEntry<Integer> anIntegerEntry;
    public static ConfigBuilder.ConfigEntry<Double> aDoubleEntry;
    public static ConfigBuilder.ConfigEntry<String> aStringEntry;
    public static ConfigBuilder.ConfigEntry<List<Integer>> anIntegerListEntry;
    public static ConfigBuilder.ConfigEntry<TestEnum> anEnumEntry;

    public static void main(String[] args) {
        ConfigBuilder.create(new File("./test.properties").toPath(), configBuilder -> {
            aBooleanEntry = configBuilder.booleanEntry("boolean_entry", false);
            anIntegerEntry = configBuilder.integerEntry("integer_entry", 50, 0, 100);
            aDoubleEntry = configBuilder.doubleEntry("double_entry", 50D, 0D, 100D);
            aStringEntry = configBuilder.stringEntry("string_entry", "test");
            anIntegerListEntry = configBuilder.integerListEntry("integer_list_entry", Collections.singletonList(5));
            anEnumEntry = configBuilder.enumEntry("enum_entry", TestEnum.TEST_1);
        });

        System.out.println(anEnumEntry.get().name());
        System.out.println(aDoubleEntry.get());
        aDoubleEntry.set(10D).save();
        System.out.println(aDoubleEntry.get());
    }

    public enum TestEnum {
        TEST_1, TEST_2, TEST_3;
    }

}
