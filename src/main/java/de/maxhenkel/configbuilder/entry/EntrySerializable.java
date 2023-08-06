package de.maxhenkel.configbuilder.entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntrySerializable {

    Class<? extends ValueSerializer<?>> value();

}
