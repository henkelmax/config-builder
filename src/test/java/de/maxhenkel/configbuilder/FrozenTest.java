package de.maxhenkel.configbuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class FrozenTest {

    @Test
    @DisplayName("Frozen builder")
    void frozenBuilder() {
        AtomicReference<ConfigBuilder> builderRef = new AtomicReference<>(null);
        ConfigBuilder.builder(configBuilder -> {
            builderRef.set(configBuilder);
            return null;
        }).build();

        assertNotNull(builderRef.get(), "Builder is null");
        assertThrowsExactly(IllegalStateException.class, () -> {
            builderRef.get().booleanEntry("test", false);
        });
    }

}
