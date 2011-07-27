package com.dumptruckman.bartersigns.entity.player;

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

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setStockItem(player, heldItem);
                    barterSign.setPhase(BarterSign.SignPhase.SETUP_PAYMENT);
                    barterSign.activatePaymentPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setPaymentItem(player, heldItem);
                    barterSign.setPhase(BarterSign.SignPhase.READY);
                    barterSign.activateReadyPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {
                    barterSign.doSelectedMenuItem(player);
                } else {

                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {
                    
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {

                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                // @TODO or admin
                if (player.getName().equals(barterSign.getOwner())) {
                    barterSign.cycleMenu();
                    barterSign.showMenu(player);
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
        if (event.getNewSlot() == event.getPreviousSlot()) return;;

        // @TODO or admin
        if (event.getPlayer().getName().equals(barterSign.getOwner())) {
            //@TODO change this to increase sell/accept amounts.. or something.
            barterSign.setMenuIndex(event.getNewSlot());
            barterSign.showMenu(event.getPlayer());
        }
    }
}
