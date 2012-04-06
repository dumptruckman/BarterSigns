package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;

/**
 * @author dumptruckman
 */
public class HelpMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public HelpMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_HELP.getPath(), " "));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    //@Override
    //public void onSelect(CommandSender sender) {
    //    if (sender == null) return;
    //    Player player = (Player) sender;
    //}

    public void run() {
        plugin.sendMessage(player, LanguagePath.PLAYER_HELP.getPath());
    }
}
