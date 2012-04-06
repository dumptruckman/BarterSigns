package com.dumptruckman.minecraft.bartersigns;

import com.dumptruckman.minecraft.bartersigns.api.BSConfig;
import com.dumptruckman.minecraft.bartersigns.util.CommentedConfig;
import com.dumptruckman.minecraft.pluginbase.plugin.AbstractBukkitPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BarterSignsPlugin extends AbstractBukkitPlugin<BSConfig> {

    private final List<String> cmdPrefixes = Arrays.asList("bs");

    @Override
    public List<String> getCommandPrefixes() {
        return cmdPrefixes;
    }

    @Override
    protected BSConfig newConfigInstance() throws IOException {
        return new CommentedConfig(this, new File(getDataFolder(), "config.yml"), BSConfig.class);
    }
}
