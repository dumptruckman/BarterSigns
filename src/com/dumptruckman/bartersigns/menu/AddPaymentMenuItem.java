package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.inventory.ItemStack;

/**
 * @author dumptruckman
 */
public class AddPaymentMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AddPaymentMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(), ""));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        if (player != null) {
            setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(),
                    Integer.toString(barterSign.getAcceptableItems()
                            .get(barterSign.indexOf(player.getItemInHand())).getAmount())));
        }
    }

    public void run() {
        ItemStack item = player.getItemInHand();
        Integer amount = 1;
        if (!barterSign.contains(item)) {
            barterSign.addAcceptableItem(player, item);
        } else {
            amount = barterSign.increaseAcceptableItemAmount(item);
        }
        this.setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(), amount.toString()));
        barterSign.showMenu(player);
    }
}
