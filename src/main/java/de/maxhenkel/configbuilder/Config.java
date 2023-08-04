package de.maxhenkel.configbuilder;

import java.util.Map;

public interface Config {

    /**
     * @return an unmodifiable map containing all config entries
     */
    Map<String, String> getEntries();

}
