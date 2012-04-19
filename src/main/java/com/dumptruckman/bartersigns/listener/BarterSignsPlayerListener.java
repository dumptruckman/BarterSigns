package com.dumptruckman.bartersigns.listener;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.config.ConfigPath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import com.dumptruckman.bartersigns.sign.BarterSignManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import static com.dumptruckman.bartersigns.locale.LanguagePath.*;


/**
 * @author dumptruckman
 */
public class BarterSignsPlayerListener implements Listener {

    private BarterSignsPlugin plugin;

    public BarterSignsPlayerListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;

        BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getClickedBlock());
        if (barterSign == null) return;

        if (plugin.config.getConfig().getBoolean(ConfigPath.PLUGINS_OVERRIDE.getPath(), (Boolean) ConfigPath.PLUGINS_OVERRIDE.getDefault()))
            if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase()))
                if (barterSign.getMenuIndex() != barterSign.REMOVE)
                    event.setCancelled(true);

        Player player = event.getPlayer();
        if (plugin.config.getConfig().getBoolean(ConfigPath.USE_PERMS.getPath(), (Boolean) ConfigPath.USE_PERMS.getDefault())) {
            if (!event.getPlayer().hasPermission("bartersigns.use")) {
                plugin.sendMessage(player, NO_PERMISSION.getPath());
                return;
            }
        }

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.isOp()
                        || (plugin.config.getConfig().getBoolean(ConfigPath.USE_PERMS.getPath(),
                        (Boolean) ConfigPath.USE_PERMS.getDefault()) && player.hasPermission("bartersigns.admin"))) {
                    ItemStack heldItem = player.getItemInHand();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        plugin.sendMessage(player, NO_ITEM_IN_HAND.getPath());
                        return;
                    }
                    barterSign.setSellableItem(player, new ItemStack(heldItem.getType(), 1, heldItem.getDurability()));
                    barterSign.activateReadyPhase(player);
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.isOp()
                        || (plugin.config.getConfig().getBoolean(ConfigPath.USE_PERMS.getPath(),
                        (Boolean) ConfigPath.USE_PERMS.getDefault()) && player.hasPermission("bartersigns.admin"))) {
                    barterSign.doSelectedMenuItem(player);
                } else {
                    barterSign.buy(player);
                }
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (BarterSign.SignPhase.SETUP_STOCK.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.isOp()
                        || (plugin.config.getConfig().getBoolean(ConfigPath.USE_PERMS.getPath(),
                        (Boolean) ConfigPath.USE_PERMS.getDefault()) && player.hasPermission("bartersigns.admin"))) {
                    return;
                } else {
                    plugin.sendMessage(player, SIGN_SETUP_UNFINISHED.getPath(), barterSign.getOwner());
                    return;
                }
            } else if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                if (player.getName().equals(barterSign.getOwner()) || player.isOp()
                        || (plugin.config.getConfig().getBoolean(ConfigPath.USE_PERMS.getPath(),
                        (Boolean) ConfigPath.USE_PERMS.getDefault()) && player.hasPermission("bartersigns.admin"))) {
                    barterSign.cycleMenu(player, player.isSneaking());
                    barterSign.showMenu(player);
                } else {
                    barterSign.showInfo(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign)) return;
        BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(block);
        if (barterSign == null) return;
        if (!BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) return;
        if (barterSign.getMenuIndex() > 0) {
            barterSign.setMenuIndex(player, barterSign.getMenuIndex());
        }
    }
/*
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Block block = event.getPlayer().getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign)) return;

        BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(block);

        if (!barterSign.exists()) return;
        if (!BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) return;
        if (event.getNewSlot() == event.getPreviousSlot()) return;

        Player player = event.getPlayer();
        if (player.getName().equals(barterSign.getOwner()) || player.isOp()
                        || (plugin.config.getBoolean(ConfigPath.USE_PERMS.getPath(),
                        (Boolean)ConfigPath.USE_PERMS.getDefault()) && player.hasPermission("bartersigns.admin"))) {
            //@TODO change this to increase sell/accept amounts.. or something.
            barterSign.setMenuIndex(player, event.getNewSlot());
            barterSign.showMenu(player);
        }
    }*/
}
