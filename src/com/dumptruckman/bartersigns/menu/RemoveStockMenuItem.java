package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.block.BarterSign;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dumptruckman
 */
public class RemoveStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;
    private ItemStack sellableItem;

    public RemoveStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_REMOVE_STOCK.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
        update();
    }

    @Override public void update() {
        sellableItem = barterSign.getSells();
    }

    public void run() {
        if (barterSign.getStock() >= sellableItem.getAmount()) {
            System.out.println(sellableItem);
            HashMap<Integer, ItemStack> itemsLeftOver = player.getInventory().addItem(
                    new ItemStack(sellableItem.getType(), sellableItem.getAmount(), sellableItem.getDurability()));
            int amountLeftOver = 0;
            for (Map.Entry<Integer, ItemStack> item : itemsLeftOver.entrySet()) {
                System.out.println("oh");
                amountLeftOver += item.getValue().getAmount();
            }
            //System.out.println(amountLeftOver);
            barterSign.setStock(barterSign.getStock() - (sellableItem.getAmount() - amountLeftOver));
            if (amountLeftOver > 0) {
                plugin.sendMessage(player, LanguagePath.SIGN_STOCK_COLLECT_LEFTOVER.getPath());
            }
            if (amountLeftOver != sellableItem.getAmount()) {
                this.setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
            }
            barterSign.showMenu(player);
        } else {
            plugin.sendMessage(player, LanguagePath.SIGN_INSUFFICIENT_STOCK.getPath());
        }
    }
}
