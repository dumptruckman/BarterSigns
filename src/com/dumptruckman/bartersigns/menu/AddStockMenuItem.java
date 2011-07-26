package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.block.BarterSign;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import org.bukkit.inventory.ItemStack;


/**
 * @author dumptruckman
 */
public class AddStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;
    private ItemStack sellableItem;

    public AddStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
        sellableItem = barterSign.getSells();
    }

    public void updateSellableItem() {
        sellableItem = barterSign.getSells();
    }

    public void run() {
        System.out.println(player + " " + sellableItem);
        InventoryTools.remove(player.getInventory(), sellableItem.getType(), sellableItem.getDurability(),
                sellableItem.getAmount());
        barterSign.setStock(barterSign.getStock() + sellableItem.getAmount());
    }
}
