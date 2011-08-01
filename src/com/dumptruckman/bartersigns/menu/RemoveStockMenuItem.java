package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dumptruckman
 */
public class RemoveStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public RemoveStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_REMOVE_STOCK.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_REMOVE_STOCK.getPath(), barterSign.getStock().toString()));
    }

    public void run() {
        if (barterSign.getStock() > 0) { //barterSign.getSellableItem().getAmount()
            int amount = barterSign.getSellableItem().getAmount();
            if (barterSign.getStock() < amount) {
                amount = barterSign.getStock();
            }
            HashMap<Integer, ItemStack> itemsLeftOver = player.getInventory().addItem(
                    new ItemStack(barterSign.getSellableItem().getType(), amount,
                            barterSign.getSellableItem().getDurability()));
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
