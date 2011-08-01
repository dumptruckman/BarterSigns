package com.dumptruckman.actionmenu;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.plugin2.main.server.Plugin;

import java.util.logging.Logger;

/**
 * @author dumprtuckman
 */
public class SignActionMenu extends ActionMenu {

    protected Block sign;

    public SignActionMenu(Block sign) {
        this.sign = sign;
    }

    public void showMenu(CommandSender sender) {
        showSelectedMenuItem(sender);
    }

    public void showSelectedMenuItem(CommandSender sender) {
        Sign sign = null;
        try {
            sign = (Sign) this.sign.getState();
        } catch (ClassCastException e) {
            Logger.getLogger("Minecraft.ActionMenu").severe("Tried to show a SignActionMenu on a non-sign block.");
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, ((SignActionMenuItem) this.getSelectedMenuItem()).getLine(i));
        }
        if (sender instanceof Player && sender != null) {
            ((Player) sender).sendBlockChange(sign.getBlock().getLocation(), 0, (byte) 0);
        }
        sign.update(true);
    }

    /**
     * Perform menu item for sender at selectedIndex.  The menu item must implement Runnable.
     *
     * @param sender whoever is activating the menu item.  MUST be a player!
     * @param index  selectedIndex of the menu item
     * @return the item performed
     */
    @Override
    public ActionMenuItem doMenuItem(CommandSender sender, int index) {
        ActionMenuItem selectedItem = contents.get(index);
        if ((selectedItem instanceof SignActionMenuItem) && (sender instanceof Player)) {
            ((SignActionMenuItem) selectedItem).setInteractingPlayer((Player) sender);
            ((SignActionMenuItem) selectedItem).run();
        }
        return selectedItem;
    }
}
