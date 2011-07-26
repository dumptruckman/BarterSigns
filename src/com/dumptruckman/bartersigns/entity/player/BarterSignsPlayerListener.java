package com.dumptruckman.bartersigns.entity.player;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.block.BarterSign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;


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

        BarterSign barterSign = new BarterSign(plugin, event.getClickedBlock());
        if (!barterSign.exists()) return;
        if (!event.getPlayer().getName().equals(barterSign.getOwner())) return;

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (barterSign.getPhase() == BarterSign.SignPhase.SETUP_STOCK) {
                Material heldItem = event.getPlayer().getItemInHand().getType();
                Inventory playerInventory = event.getPlayer().getInventory();
                //@TODO playerInventory.
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (barterSign.getPhase() == BarterSign.SignPhase.SETUP_STOCK) {

            }
        }
    }
}
