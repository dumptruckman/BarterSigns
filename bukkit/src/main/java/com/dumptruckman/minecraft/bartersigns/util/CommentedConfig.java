package com.dumptruckman.minecraft.bartersigns.util;

import com.dumptruckman.minecraft.bartersigns.BarterSignsPlugin;
import com.dumptruckman.minecraft.bartersigns.api.BSConfig;
import com.dumptruckman.minecraft.pluginbase.config.AbstractYamlConfig;

import java.io.File;
import java.io.IOException;

public class CommentedConfig extends AbstractYamlConfig<BSConfig> implements BSConfig {

    public CommentedConfig(BarterSignsPlugin plugin, File configFile, Class<? extends BSConfig>... configClasses) throws IOException {
        super(plugin, true, configFile, configClasses);
    }

    @Override
    protected String getHeader() {
        return "";
    }
}
