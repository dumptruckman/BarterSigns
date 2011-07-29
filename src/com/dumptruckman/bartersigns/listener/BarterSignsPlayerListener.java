package com.dumptruckman.bartersigns.listener;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import static com.dumptruckman.bartersigns.locale.LanguagePath.NO_ITEM_IN_HAND;
import static com.dumptruckman.bartersigns.locale.LanguagePath.NO_PERMISSION;
import static com.dumptruckman.bartersigns.locale.LanguagePath.SIGN_SETUP_UNFINISHED;


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

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(event.getClickedBlock());
        if (!barterSign.exists()) return;

        Player player = event.getPlayer();
        if (!event.getPlayer().hasPermission("bartersigns.use")) {
            plugin.sendMessage(player, NO_PERMISSION.getPath());
            return;
        }

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setSellableItem(player, heldItem);
                    barterSign.activateReadyPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                // This part is probably useless now.
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.addAcceptableItem(player, heldItem);
                    barterSign.activateReadyPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    barterSign.doSelectedMenuItem(player);
                } else {
                    barterSign.buy(player);
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    return;
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                // This part is probably useless now.
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    return;
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersigns.admin")) {
                    barterSign.cycleMenu(player);
                    barterSign.showMenu(player);
                } else {
                    barterSign.showInfo(player);
                }
            }
        }
    }

    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Block block = event.getPlayer().getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign)) return;

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(block);
        if (!barterSign.exists()) return;
        if (!BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) return;
        if (event.getNewSlot() == event.getPreviousSlot()) return;

        if (event.getPlayer().getName().equals(barterSign.getOwner()) || event.getPlayer().hasPermission("bartersign.admin")) {
            //@TODO change this to increase sell/accept amounts.. or something.
            barterSign.setMenuIndex(event.getPlayer(), event.getNewSlot());
            barterSign.showMenu(event.getPlayer());
        }
    }
}
