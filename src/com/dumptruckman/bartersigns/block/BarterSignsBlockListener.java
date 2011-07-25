package com.dumptruckman.bartersigns.block;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author dumptruckman
 */
public class BarterSignsBlockListener extends BlockListener {

    private BarterSignsPlugin plugin;

    public BarterSignsBlockListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        // Get some show stoppers out of the way
        if (event.isCancelled()) return;
        if (!(event.getBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign)event.getBlock().getState();
        System.out.println("test");
        if (!sign.getLine(0).equalsIgnoreCase("[Barter]")) return;

        
    }
}
