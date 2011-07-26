package com.dumptruckman.bartersigns.block;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author dumptruckman
 */
public class BarterSignsBlockListener extends BlockListener {

    private BarterSignsPlugin plugin;

    public BarterSignsBlockListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onSignChange(SignChangeEvent event) {
        // Throw out unimportant events immediately
        if (event.isCancelled()) return;
        if (!event.getLine(0).equalsIgnoreCase("[Barter]")) return;

        BarterSign barterSign = new BarterSign(plugin, event.getBlock());
        barterSign.clear();
        barterSign.init(event.getPlayer());
        event.setLine(0, event.getPlayer().getName());
        event.setLine(1, "L-click with");
        event.setLine(2, "item you want");
        event.setLine(3, "to sell");
    }
}
