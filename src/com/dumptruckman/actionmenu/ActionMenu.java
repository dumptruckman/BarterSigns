package com.dumptruckman.actionmenu;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dumptruckman
 */
public abstract class ActionMenu {

    protected List<ActionMenuItem> contents = new ArrayList<ActionMenuItem>();
    protected int selectedIndex = 0;
    protected List<String> header = new ArrayList<String>();
    protected List<String> footer = new ArrayList<String>();

    /**
     * Set's the text to go before the menu options.
     * @param firstLine First line of header.
     * @param additionalLines Optional additional lines of header.
     */
    public void setHeader(String firstLine, String...additionalLines) {
        header.clear();
        header.add(firstLine);

        if (additionalLines != null) {
            for (String line : additionalLines) {
                header.add(line);
            }
        }
    }
    
    /**
     * Returns the header for this menu.
     * @return List of header lines.
     */
    public List<String> getHeader() {
        return header;
    }

    /**
     * Set's the text to go after the menu options.
     * @param firstLine First line of footer.
     * @param additionalLines Optional additional lines of footer.
     */
    public void setFooter(String firstLine, String...additionalLines) {
        footer.clear();
        footer.add(firstLine);

        if (additionalLines != null) {
            for (String line : additionalLines) {
                footer.add(line);
            }
        }
    }

    /**
     * Returns the footer for this menu.
     * @return List of footer lines.
     */
    public List<String> getFooter() {
        return footer;
    }

    /**
     * Specify a list of contents for this menu.
     * @param contents List of menu items to set for this menu.
     */
    public void setContents(List<ActionMenuItem> contents) {
        this.contents = contents;
    }

    /**
     * Retrieve the underlying ArrayList of menu items.
     * @return Menu item list.
     */
    public List<ActionMenuItem> getContents() {
        return contents;
    }

    /**
     * Returns the menu item that is selected.
     * @return Selected menu item.
     */
    public ActionMenuItem getSelectedMenuItem() {
        return contents.get(selectedIndex);
    }

    /**
     * Get the index of the current menu selection.
     * @return The selected menu item's index.
     */
    public Integer getMenuIndex() {
        return selectedIndex;
    }

    /**
     * Cycles the selection through the menu options.
     */
    public void cycleMenu() {
        cycleMenu(false);
    }

    /**
     * Cycles the selection through the menu options.
     * @param reverse If set to true, cycles backwards.
     */
    public void cycleMenu(boolean reverse) {
        if (reverse) {
            selectedIndex--;
        } else {
            selectedIndex++;
        }
        if (selectedIndex < 0) {
            selectedIndex = contents.size() - 1;
        }
        if (selectedIndex >= contents.size()) {
            selectedIndex = 0;
        }
    }

    /**
     * Sets the current menu selection to specified index.
     * @param index Sets the selection index to this.
     */
    public void setMenuIndex(int index) {
        selectedIndex = index;
    }

    /**
     * Perform doMenuItem() of the menu at specific index for the sender.
     * @param sender Whoever is activating the menu item. This could be null if the sender is not important for the task.
     * @param index Index of the menu item to perform.
     * @return The item performed.
     */
    public ActionMenuItem doMenuItem(CommandSender sender, int index) {
        ActionMenuItem selectedItem = contents.get(index);
        selectedItem.run();
        return selectedItem;
    }

    /**
     * Performs doMenuItem() on the currently selected menu item for the sender.
     * @param sender Whoever is activating the menu item. This could be null if the sender is not important for the task.
     * @return the item performed
     */
    public ActionMenuItem doSelectedMenuItem(CommandSender sender) {
        return doMenuItem(sender, selectedIndex);
    }

    /**
     * Runs the update method on all menu items.
     */
    public void updateMenuItems() {
        for (ActionMenuItem item : contents) {
            item.update();
        }
    }

    /**
     * Shows the menu to a CommandSender.
     * @param sender CommandSender to show menu to.  Possibly null depending on implementation.
     */
    public abstract void showMenu(CommandSender sender);
}
