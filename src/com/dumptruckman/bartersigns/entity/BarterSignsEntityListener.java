package com.dumptruckman.bartersigns.entity;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.event.entity.EntityListener;

/**
 * @author dumptruckman
 */
public class BarterSignsEntityListener extends EntityListener {

    private BarterSignsPlugin plugin;

    public BarterSignsEntityListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }
}
