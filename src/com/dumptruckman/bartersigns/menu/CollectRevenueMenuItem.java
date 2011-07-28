package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
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
        List<ItemStack> acceptItems = barterSign.getAcceptableItems();
        boolean hasRevenue = false;
        for (ItemStack acceptItem : acceptItems) {
            if (barterSign.getRevenue(acceptItem) > 0) {
                if (!hasRevenue) hasRevenue = true;
                HashMap<Integer, ItemStack> itemsLeftOver = player.getInventory().addItem(new ItemStack(
                        acceptItem.getType(),
                        barterSign.getRevenue(acceptItem),
                        acceptItem.getDurability()));
                int amountLeftOver = 0;
                for (Map.Entry<Integer, ItemStack> item : itemsLeftOver.entrySet()) {
                    amountLeftOver += item.getValue().getAmount();
                }
                barterSign.setRevenue(acceptItem, barterSign.getRevenue(acceptItem)
                        - (barterSign.getRevenue(acceptItem) - amountLeftOver));
                if (amountLeftOver > 0) {
                    plugin.sendMessage(player, LanguagePath.SIGN_COLLECT_LEFTOVER.getPath());
                    break;
                }
            }
        }
        if (!hasRevenue) {
            plugin.sendMessage(player, LanguagePath.SIGN_REVENUE_EMPTY.getPath());
        }
    }
}
