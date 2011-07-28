package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dumptruckman
 */
public class CollectRevenueMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public CollectRevenueMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_MENU_COLLECT_REVENUE.getPath()));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void run() {
        if (barterSign.getRevenue(barterSign.getAcceptableItem()) > 0) {
            HashMap<Integer, ItemStack> itemsLeftOver = player.getInventory().addItem(new ItemStack(
                    barterSign.getAcceptableItem().getType(),
                    barterSign.getRevenue(barterSign.getAcceptableItem()),
                    barterSign.getAcceptableItem().getDurability()));
            int amountLeftOver = 0;
            for (Map.Entry<Integer, ItemStack> item : itemsLeftOver.entrySet()) {
                amountLeftOver += item.getValue().getAmount();
            }
            barterSign.setRevenue(barterSign.getAcceptableItem(), barterSign.getRevenue(barterSign.getAcceptableItem())
                    - (barterSign.getRevenue(barterSign.getAcceptableItem()) - amountLeftOver));
            if (amountLeftOver > 0) {
                plugin.sendMessage(player, LanguagePath.SIGN_COLLECT_LEFTOVER.getPath());
            }
        } else {
            plugin.sendMessage(player, LanguagePath.SIGN_REVENUE_EMPTY.getPath());
        }
    }
}
