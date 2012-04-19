package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.config.ConfigPath;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dumptruckman
 */
public class AlterStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AlterStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void onSelect(CommandSender sender) {
        super.onSelect(sender);
        if (sender == null) return;
        final Player player = (Player) sender;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!player.isSneaking()) {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(),
                            barterSign.getStock().toString()));
                } else {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_REMOVE_STOCK.getPath(),
                            barterSign.getStock().toString()));
                }
                barterSign.showMenu(player);
            }
        });
    }

    public void run() {
        if (!player.isSneaking()) {
            // Add stock
            int limit = plugin.config.getConfig().getInt(ConfigPath.SIGN_STORAGE_LIMIT.getPath(),
                    (Integer) ConfigPath.SIGN_STORAGE_LIMIT.getDefault());

            // Check if sign is already at/over limit
            if (plugin.stockLimitUsesStacks()) {
                if (limit != 0 && barterSign.getStock()/barterSign.getSellableItem().getType().getMaxStackSize() >= limit) {
                    plugin.sendMessage(player, LanguagePath.SIGN_STOCK_LIMIT.getPath());
                    return;
                }
            } else {
                if (limit != 0 && barterSign.getStock() >= limit) {
                    plugin.sendMessage(player, LanguagePath.SIGN_STOCK_LIMIT.getPath());
                    return;
                }
            }

            if (InventoryTools.remove(player.getInventory(), barterSign.getSellableItem().getType(),
                    barterSign.getSellableItem().getDurability(), barterSign.getSellableItem().getAmount())) {
                // The sellable item was removed from the player's inventory
                
                barterSign.setStock(barterSign.getStock() + barterSign.getSellableItem().getAmount());
                if (limit != 0) {
                    if ((plugin.stockLimitUsesStacks()
                                && barterSign.getStock()/barterSign.getSellableItem().getType().getMaxStackSize() > limit)) {
                        int overBy = (barterSign.getStock()/barterSign.getSellableItem().getType().getMaxStackSize()) - limit;
                        for (int i = 0; i < overBy; i++) {
                            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(
                                    barterSign.getSellableItem().getType(),
                                    barterSign.getSellableItem().getType().getMaxStackSize(),
                                    barterSign.getSellableItem().getDurability()));
                            barterSign.spawnItemsAtSign(leftover);
                        }
                        barterSign.setStock(limit);
                        plugin.sendMessage(player, LanguagePath.SIGN_STOCK_LIMIT.getPath());
                    }
                    if (!plugin.stockLimitUsesStacks()
                                && barterSign.getStock() > limit) {
                        int overBy = barterSign.getStock() - limit;
                        HashMap<Integer, ItemStack> leftover = null;
                        if (plugin.enforceMaxStackSize()) {
                            List<ItemStack> items = InventoryTools.getSeparatedItems(new ItemStack(
                                    barterSign.getSellableItem().getType(), overBy,
                                    barterSign.getSellableItem().getDurability()));
                            leftover = player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                        } else {
                            leftover = player.getInventory().addItem(new ItemStack(
                                    barterSign.getSellableItem().getType(), overBy,
                                    barterSign.getSellableItem().getDurability()));
                        }
                        barterSign.spawnItemsAtSign(leftover);
                        barterSign.setStock(limit);
                        plugin.sendMessage(player, LanguagePath.SIGN_STOCK_LIMIT.getPath());
                    }
                }
                this.setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
                barterSign.showMenu(player);
            } else {
                String item = plugin.itemToString(barterSign.getSellableItem(), false);
                plugin.sendMessage(player, LanguagePath.PLAYER_INSUFFICIENT_AMOUNT.getPath(), item);
            }
        } else {
            // Remove stock
            if (barterSign.getStock() > 0) { //barterSign.getSellableItem().getAmount()
                int amount = barterSign.getSellableItem().getAmount();
                if (barterSign.getStock() < amount) {
                    amount = barterSign.getStock();
                }
                HashMap<Integer, ItemStack> itemsLeftOver = null;
                if (plugin.enforceMaxStackSize()) {
                    List<ItemStack> items = InventoryTools.getSeparatedItems(
                            new ItemStack(barterSign.getSellableItem().getType(), amount,
                            barterSign.getSellableItem().getDurability()));
                    itemsLeftOver = player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                } else {
                    itemsLeftOver = player.getInventory().addItem(
                            new ItemStack(barterSign.getSellableItem().getType(), amount,
                            barterSign.getSellableItem().getDurability()));
                }
                int amountLeftOver = 0;
                for (Map.Entry<Integer, ItemStack> item : itemsLeftOver.entrySet()) {
                    amountLeftOver += item.getValue().getAmount();
                }
                barterSign.setStock(barterSign.getStock() - (amount - amountLeftOver));
                if (amountLeftOver > 0) {
                    plugin.sendMessage(player, LanguagePath.SIGN_COLLECT_LEFTOVER.getPath());
                }
                if (amountLeftOver != barterSign.getSellableItem().getAmount()) {
                    this.setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_REMOVE_STOCK.getPath(), barterSign.getStock().toString()));
                }
                barterSign.showMenu(player);
            } else {
                plugin.sendMessage(player, LanguagePath.SIGN_INSUFFICIENT_STOCK.getPath());
            }
        }
    }
}
