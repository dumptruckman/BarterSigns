package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.inventory.ItemStack;


/**
 * @author dumptruckman
 */
public class AddStockMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;
    private ItemStack sellableItem;

    public AddStockMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
        this.plugin = plugin;
        this.barterSign = barterSign;
        update();
    }

    @Override public void update() {
        sellableItem = barterSign.getSells();
    }

    public void run() {
        if (InventoryTools.remove(player.getInventory(), sellableItem.getType(), sellableItem.getDurability(),
                sellableItem.getAmount())) {
            barterSign.setStock(barterSign.getStock() + sellableItem.getAmount());
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_MENU_ADD_STOCK.getPath(), barterSign.getStock().toString()));
            barterSign.showMenu(player);
        } else {
            String item = sellableItem.getType().toString();
            if (sellableItem.getDurability() != 0) {
                item += sellableItem.getDurability();
            }
            plugin.sendMessage(player, LanguagePath.PLAYER_INSUFFICIENT_AMOUNT.getPath(), item);
        }
    }
}
