package com.dumptruckman.bartersigns.block;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.event.block.BlockListener;

/**
 * @author dumptruckman
 */
public class BarterSignsBlockListener extends BlockListener {

    private BarterSignsPlugin plugin;

    public BarterSignsBlockListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }
}
