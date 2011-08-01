package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.bartersigns.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dumptruckman
 */
public class AlterSellableMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AlterSellableMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void update() {
        setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(),
                Integer.toString(barterSign.getSellableItem().getAmount())));
    }

    @Override
    public void onSelect(CommandSender sender) {
        super.onSelect(sender);
        if (sender == null) return;
        final Player player = (Player) sender;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!player.isSneaking()) {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(),
                            Integer.toString(barterSign.getSellableItem().getAmount())));
                } else {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_DECREASE.getPath(),
                            Integer.toString(barterSign.getSellableItem().getAmount())));
                }
                barterSign.showMenu(player);
            }
        });
    }

    public void run() {
        if (!player.isSneaking()) {
            Integer amount = barterSign.increaseSellableItemAmount();
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_INCREASE.getPath(), amount.toString()));
            barterSign.showMenu(player);
        } else {
            if (barterSign.getSellableItem().getAmount() > 1) {
                Integer amount = barterSign.decreaseSellableItemAmount();
                this.setLines(plugin.lang.lang(LanguagePath.SIGN_SELLABLE_DECREASE.getPath(), amount.toString()));
                barterSign.showMenu(player);
            } else {
                plugin.sendMessage(player, LanguagePath.SIGN_SELLABLE_MINIMUM.getPath());
            }
        }
    }
}
