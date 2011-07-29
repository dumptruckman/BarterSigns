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
public class RemovePaymentMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public RemovePaymentMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
        super(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(), " "));
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    @Override
    public void update() {
        if (player == null) return;
        int index = barterSign.indexOf(player.getItemInHand());
        if (index == -1) return;
        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(),
                Integer.toString(barterSign.getAcceptableItems()
                        .get(index).getAmount())));
    }

    @Override
    public void onSelect(CommandSender sender) {
        if (sender == null) return;
        Player player = (Player) sender;
        int index = barterSign.indexOf(player.getItemInHand());
        if (index == -1) return;
        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(),
                Integer.toString(barterSign.getAcceptableItems()
                        .get(index).getAmount())));
    }

    public void run() {
        ItemStack item = new ItemStack(player.getItemInHand().getType(), player.getItemInHand().getAmount(),
                player.getItemInHand().getDurability());
        int index = barterSign.indexOf(item);
        if (index != -1) {
            Integer amount = 0;
            if (barterSign.getAcceptableItems().get(index).getAmount() == 1) {
                barterSign.removeAcceptableItem(player, item);
            } else {
                amount = barterSign.decreaseAcceptableItemAmount(item);
            }
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(), amount.toString()));
            barterSign.showMenu(player);
        } else {
            plugin.sendMessage(player, LanguagePath.PLAYER_UNACCEPTABLE_ITEM.getPath());
        }
    }
}
