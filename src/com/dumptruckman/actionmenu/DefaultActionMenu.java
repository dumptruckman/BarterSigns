package com.dumptruckman.actionmenu;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author dumptruckman
 */
public class DefaultActionMenu extends ActionMenu {

    public DefaultActionMenu(String title) {
        super(title);
    }

    public DefaultActionMenu(String title, List<ActionMenuItem> menuContents) {
        super(title);
        setContents(menuContents);
    }

    public void showMenu(CommandSender sender) {
        sender.sendMessage("=======================");
        for (String header : getHeader()) {
            sender.sendMessage(header);
        }
        for (int i = 0; i < contents.size(); i++) {
            if (i == selectedIndex) {
                sender.sendMessage(" * " + contents.get(i).toString());
            } else {
                sender.sendMessage(contents.get(i).toString());
            }
        }
        for (String footer : getFooter()) {
            sender.sendMessage(footer);
        }
        sender.sendMessage("=======================");
    }
}
