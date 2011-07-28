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
import static com.dumptruckman.bartersigns.locale.LanguagePath.SIGN_INFO;
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
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setSellableItem(player, heldItem);
                    barterSign.setPhase(BarterSign.SignPhase.SETUP_PAYMENT);
                    barterSign.activatePaymentPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setAcceptableItem(player, heldItem);
                    barterSign.setPhase(BarterSign.SignPhase.READY);
                    barterSign.activateReadyPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    barterSign.doSelectedMenuItem(player);
                } else {
                    barterSign.buy(player);
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    return;
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.SETUP_PAYMENT.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    return;
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.hasPermission("bartersign.admin")) {
                    barterSign.cycleMenu();
                    barterSign.showMenu(player);
                } else {
                    ItemStack sellItem = barterSign.getSellableItem();
                    ItemStack acceptItem = barterSign.getAcceptableItem();
                    String sellItemString = sellItem.getType().toString();
                    if (sellItem.getDurability() != 0) {
                        sellItemString += "(" + sellItem.getDurability() + ")";
                    }
                    String acceptItemString = acceptItem.getAmount() + " " + acceptItem.getType().toString();
                    if (acceptItem.getDurability() != 0) {
                        acceptItemString += "(" + acceptItem.getDurability() + ")";
                    }
                    plugin.sendMessage(player, SIGN_INFO.getPath(), sellItemString, acceptItemString,
                            Integer.toString(sellItem.getAmount()));
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
            barterSign.setMenuIndex(event.getNewSlot());
            barterSign.showMenu(event.getPlayer());
        }
    }
}
