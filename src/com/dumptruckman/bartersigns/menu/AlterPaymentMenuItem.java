package com.dumptruckman.bartersigns.menu;

import com.dumptruckman.actionmenu.SignActionMenuItem;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import com.dumptruckman.bartersigns.sign.BarterSignManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author dumptruckman
 */
public class AlterPaymentMenuItem extends SignActionMenuItem {

    private BarterSignsPlugin plugin;
    private BarterSign barterSign;

    public AlterPaymentMenuItem(BarterSignsPlugin plugin, BarterSign barterSign) {
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
        super.onSelect(sender);
        if (sender == null) return;
        final Player player = (Player) sender;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.isSneaking()) {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(), " "));
                } else {
                    setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(), " "));
                }
                int index = barterSign.indexOf(player.getItemInHand());
                if (index != -1) {
                    if (player.isSneaking()) {
                        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_REMOVE.getPath(),
                                Integer.toString(barterSign.getAcceptableItems()
                                .get(index).getAmount())));
                    } else {
                        setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(),
                                Integer.toString(barterSign.getAcceptableItems()
                                .get(index).getAmount())));
                    }
                }
                barterSign.showMenu(player);
            }
        });
    }

    public void run() {
        ItemStack item = new ItemStack(player.getItemInHand().getType(), player.getItemInHand().getAmount(),
                player.getItemInHand().getDurability());
        if (item.getType() == Material.AIR) {
            plugin.sendMessage(player, LanguagePath.PLAYER_UNACCEPTABLE_ITEM.getPath());
            return;
        }
        if (!player.isSneaking()) {
            Integer amount = 1;
            if (!barterSign.contains(item)) {
                barterSign.addAcceptableItem(player, item);
            } else {
                amount = barterSign.increaseAcceptableItemAmount(item);
            }
            this.setLines(plugin.lang.lang(LanguagePath.SIGN_PAYMENT_ADD.getPath(), amount.toString()));
            barterSign.showMenu(player);
        } else {
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
}
