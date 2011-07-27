package com.dumptruckman.actionmenu;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dumprtuckman
 */
public class SignActionMenu extends ActionMenu {

    protected Block sign;

    public SignActionMenu(String title, Block sign) {
        super(title);
        this.sign = sign;
    }

    public void showMenu(CommandSender sender) {
        showSelectedMenuItem();
    }

    public void showSelectedMenuItem() {
        Sign sign = (Sign)this.sign.getState();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, ((SignActionMenuItem)this.getSelectedMenuItem()).getLine(i));
        }
        sign.update();
    }

    /**
     * Perform menu item for sender at selectedIndex.  The menu item must implement Runnable.
     * @param sender whoever is activating the menu item.  MUST be a player!
     * @param index selectedIndex of the menu item
     * @return the item performed
     */
    @Override public ActionMenuItem doMenuItem(CommandSender sender, int index) {
        ActionMenuItem selectedItem = contents.get(index);
        if ((selectedItem instanceof SignActionMenuItem) && (sender instanceof Player)) {
            ((SignActionMenuItem)selectedItem).setInteractingPlayer((Player)sender);
            ((SignActionMenuItem)selectedItem).run();
        }
        return selectedItem;
    }
}
