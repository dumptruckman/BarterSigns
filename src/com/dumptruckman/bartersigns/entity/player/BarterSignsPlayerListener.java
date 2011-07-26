package com.dumptruckman.bartersigns.entity.player;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * @author dumptruckman
 */
public class BarterSignsPlayerListener extends PlayerListener {

    private BarterSignsPlugin plugin;

    public BarterSignsPlayerListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        }
    }
}
