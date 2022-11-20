package de.maxhenkel.configbuilder;

public class TestUtils {

    public static final String CONFIG_NAME = "config.properties";

    public static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
