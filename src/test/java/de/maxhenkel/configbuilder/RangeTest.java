package de.maxhenkel.configbuilder;

import de.maxhenkel.configbuilder.entry.DoubleConfigEntry;
import de.maxhenkel.configbuilder.entry.IntegerConfigEntry;
import de.maxhenkel.configbuilder.entry.LongConfigEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeTest {

    @Test
    @DisplayName("Integer range")
    void integerRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        IntegerConfigEntry entry = builder.integerEntry("integer", 0, -10, 10);

        assertEquals(-10, entry.getMin());
        assertEquals(10, entry.getMax());
        assertEquals(0, entry.get());

        entry.set(-20);
        assertEquals(-10, entry.get());

        entry.set(20);
        assertEquals(10, entry.get());
    }

    @Test
    @DisplayName("Integer no range")
    void integerNoRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        IntegerConfigEntry entry = builder.integerEntry("integer", 0);

        assertEquals(Integer.MIN_VALUE, entry.getMin());
        assertEquals(Integer.MAX_VALUE, entry.getMax());
        assertEquals(0, entry.get());

        entry.set(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, entry.get());

        entry.set(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, entry.get());
    }

    @Test
    @DisplayName("Long range")
    void longRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        LongConfigEntry entry = builder.longEntry("long", 0L, -10L, 10L);

        assertEquals(-10L, entry.getMin());
        assertEquals(10L, entry.getMax());
        assertEquals(0L, entry.get());

        entry.set(-20L);
        assertEquals(-10L, entry.get());

        entry.set(20L);
        assertEquals(10L, entry.get());
    }

    @Test
    @DisplayName("long no range")
    void longNoRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        LongConfigEntry entry = builder.longEntry("long", 0L);

        assertEquals(Long.MIN_VALUE, entry.getMin());
        assertEquals(Long.MAX_VALUE, entry.getMax());
        assertEquals(0L, entry.get());

        entry.set(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, entry.get());

        entry.set(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, entry.get());
    }

    @Test
    @DisplayName("Double range")
    void doubleRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        DoubleConfigEntry entry = builder.doubleEntry("double", 0D, -10D, 10D);

        assertEquals(-10D, entry.getMin());
        assertEquals(10D, entry.getMax());
        assertEquals(0D, entry.get());

        entry.set(-20D);
        assertEquals(-10D, entry.get());

        entry.set(20D);
        assertEquals(10D, entry.get());
    }

    @Test
    @DisplayName("Double no range")
    void doubleNoRange() {
        ConfigBuilderImpl builder = TestUtils.createInMemoryBuilder();
        DoubleConfigEntry entry = builder.doubleEntry("double", 0D);

        assertEquals(Double.MIN_VALUE, entry.getMin());
        assertEquals(Double.MAX_VALUE, entry.getMax());
        assertEquals(0D, entry.get());

        entry.set(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, entry.get());

        entry.set(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, entry.get());
    }

}
