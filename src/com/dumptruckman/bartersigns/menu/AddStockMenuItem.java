package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;


/**
 * @author dumptruckman
 */
public class AddStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AddStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
    }

    public void run() {
        if (InventoryTools.remove(player.getInventory(), barterSign.getSellableItem().getType(),
                barterSign.getSellableItem().getDurability(), barterSign.getSellableItem().getAmount())) {
            barterSign.setStock(barterSign.getStock() + barterSign.getSellableItem().getAmount());
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
            barterSign.showMenu(player);
        } else {
            String item = plugin.itemToString(barterSign.getSellableItem(), false);
            plugin.sendMessage(player, LanguagePath.PLAYER_INSUFFICIENT_AMOUNT.getPath(), item);
        }
    }
}
