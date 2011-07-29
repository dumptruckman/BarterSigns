package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dumptruckman
 */
public class AddPaymentMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AddPaymentMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(), " "));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    @Override
    public void update() {
        if (player == null) return;
        int index = barterSign.indexOf(player.getItemInHand());
        if (index == -1) return;
        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(),
                Integer.toString(barterSign.getAcceptableItems()
                        .get(index).getAmount())));
    }

    @Override
    public void onSelect(CommandSender sender) {
        if (sender == null) return;
        Player player = (Player) sender;
        int index = barterSign.indexOf(player.getItemInHand());
        if (index == -1) return;
        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(),
                Integer.toString(barterSign.getAcceptableItems()
                        .get(index).getAmount())));
    }

    public void run() {
        ItemStack item = new ItemStack(player.getItemInHand().getType(), player.getItemInHand().getAmount(),
                player.getItemInHand().getDurability());
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
