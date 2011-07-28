package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;

/**
 * @author dumptruckman
 */
public class IncreaseSellableMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public IncreaseSellableMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
    }

    public void run() {
        Integer amount = barterSign.increaseSellableItemAmount();
        this.setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(), amount.toString()));
        barterSign.showMenu(player);
    }
}
