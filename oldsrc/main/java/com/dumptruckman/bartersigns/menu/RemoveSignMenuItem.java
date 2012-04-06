package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;

/**
 * @author dumptruckman
 */
public class RemoveSignMenuItem extends MainMenuItem {

    public RemoveSignMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin, barterSign, plugin.lang.lang(LanguagePath.REMOVE_SIGN.getPath()));
    }

    @Override
    public void onSelect(CommandSender send) {
    }

    @Override
    public void run() {
    }
}
