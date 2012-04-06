package com.dumptruckman.bartersigns.actionmenu;

import org.bukkit.command.CommandSender;

/**
 * @author dumptruckman
 */
public abstract class ActionMenuItem implements Runnable {

    private String text;

    /**
     * Creates a menu item with no text.
     */
    public ActionMenuItem() {
        this("");
    }

    /**
     * Creates a menu item with specified text.
     * @param text Text for menu item.
     */
    public ActionMenuItem(String text) {
        this.text = text;
    }

    /**
     * Empty method.  Used to update menu items in some way.
     */
    public void update() {

    }

    /**
     * Empty method.  Called whenever the menu is cycled.
     * @param sender Whoever caused the cycle event.  Could be null.
     */
    protected void onCycle(CommandSender sender) {

    }

    /**
     * Empty method.  Called whenever the menu item becomes selected.
     * @param sender Whoever caused the selection event.  Could be null.
     */
    protected void onSelect(CommandSender sender) {

    }

    /**
     * Sets the menu item's text.
     * @param text Text for menu item.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the menu item's text.
     * @return Text of menu item.
     */
    public String getText() {
        return text;
    }
}
