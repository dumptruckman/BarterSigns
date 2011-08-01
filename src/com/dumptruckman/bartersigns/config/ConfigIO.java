package com.dumptruckman.bartersigns.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author dumptruckman
 */
public class ConfigIO {

    private Configuration data;

    public ConfigIO(File file) {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { }
        }
        this.data = new Configuration(file);
    }

    public ConfigIO(Configuration data) {
        this.data = data;
    }

    public void save() {
        synchronized(data) {
            data.save();
        }
    }

    public Configuration load() {
        data.load();
        return data;
    }
}
