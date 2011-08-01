package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;

/**
 * @author dumptruckman
 */
public class DecreaseSellableMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public DecreaseSellableMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_DECREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_DECREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
    }

    public void run() {
        if (barterSign.getSellableItem().getAmount() > 1) {
            Integer amount = barterSign.decreaseSellableItemAmount();
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_DECREASE.getPath(), amount.toString()));
            barterSign.showMenu(player);
        } else {
            plugin.sendMessage(player, LanguagePath.SIGN_SELLABLE_MINIMUM.getPath());
        }
    }
}
