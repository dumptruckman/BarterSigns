package com.dumptruckman.bartersigns.entity.player;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.event.player.PlayerListener;

/**
 * @author dumptruckman
 */
public class BarterSignsPlayerListener extends PlayerListener {

    private BarterSignsPlugin plugin;

    public BarterSignsPlayerListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }
}
