package com.dumptruckman.actionmenu;

import org.bukkit.command.CommandSender;

/**
 * @author dumptruckman
 */
public abstract class ActionMenuItem implements Runnable {

    private String text;

    public ActionMenuItem() {
        this("");
    }

    public ActionMenuItem(String text) {
        this.text = text;
    }

    public void update() {

    }

    public void onCycle(CommandSender sender) {

    }

    public void onSelect(CommandSender sender) {

    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
