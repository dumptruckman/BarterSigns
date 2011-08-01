package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author dumptruckman
 */
public class MainMenuItem extends SignActionMenuItem {

    protected BarterSignsPlugin plugin;
    protected BarterSign barterSign;

    public MainMenuItem(BarterSignsPlugin plugin, BarterSign barterSign, List<String> lines) {
        super(lines);
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    @Override
    public void onSelect(CommandSender sender) {
        plugin.sendMessage(player, LanguagePath.OWNER_MESSAGE.getPath());
    }

    public void run() {
        if (!player.isSneaking()) {
            barterSign.showInfo(player);
        } else {
            barterSign.buy(player);
        }
    }
}
