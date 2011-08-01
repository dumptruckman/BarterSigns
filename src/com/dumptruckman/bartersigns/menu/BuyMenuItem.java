package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;

/**
 * @author dumptruckman
 */
public class BuyMenuItem extends MainMenuItem {

    public BuyMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin, barterSign, plugin.lang.lang(LanguagePath.SIGN_PURCHASE.getPath()));
    }

    @Override
    public void onSelect(CommandSender send) {
    }

    @Override
    public void run() {

    }
}
