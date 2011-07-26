package com.dumptruckman.actionmenu;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

/**
 * @author dumprtuckman
 */
public class SignActionMenu extends ActionMenu {

    Block sign;

    public SignActionMenu(String title, Block sign) {
        super(title);
        this.sign = sign;
    }

    public void showMenu(CommandSender sender) {
        
    }
}
