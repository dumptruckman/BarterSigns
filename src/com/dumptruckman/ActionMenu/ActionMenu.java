package com.dumptruckman.actionmenu;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author dumptruckman
 */
public abstract class ActionMenu {

    protected List<Object> contents = new ArrayList<Object>();
    protected int selectedIndex = 0;
    protected List<String> header = new ArrayList<String>();
    protected List<String> footer = new ArrayList<String>();
    protected String title;

    /**
     * Creates an actionmenu with the specified title
     * @param title
     */
    public ActionMenu(String title) {
        this.title = title;
    }

    /**
     * Set's the text to go before the menu options
     * @param firstLine first line of header
     * @param additionalLines optional additional lines of header
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
     * Specify the title for this menu.  If this actionmenu is used as a menu item in another
     * actionmenu, this will be the String that appears in that menu.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the title of this actionmenu.
     * @return menu title
     */
    @Override public String toString() {
        return title;
    }

    /**
     * Returns the header for this menu
     * @return list of header lines
     */
    public List<String> getHeader() {
        return header;
    }

    /**
     * Set's the text to go after the menu options
     * @param firstLine first line of footer
     * @param additionalLines optional additional lines of footer
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
     * Returns the footer for this menu
     * @return list of footer lines
     */
    public List<String> getFooter() {
        return footer;
    }

    /**
     * Specify a list of contents for this menu
     * @param contents
     */
    public void setContents(List<Object> contents) {
        this.contents = contents;
    }

    /**
     * Appends the specified item to the end of this menu.
     * @param menuItem should be another actionmenu or Runnable that override toString().
     * @return true (as specified by Collection.add)
     */
    public boolean addMenuItem(Object menuItem) {
        return contents.add(menuItem);
    }

    /**
     * Replaces the item at the specified position in this menu with the specified item.
     * @param index index of the item to replace
     * @param menuItem should be another actionmenu or Runnable that override toString().
     * @return the item that was previously at this index
     */
    public Object setMenuItem(int index, Object menuItem) {
        return contents.set(index, menuItem);
    }

    /**
     * Retrieves the item at the specified index in the menu
     * @param index
     * @return
     */
    public Object getMenuItem(int index) {
        return contents.get(index);
    }

    /**
     * Returns the menu item that is selected
     * @return
     */
    public Object getSelectedMenuItem() {
        return contents.get(selectedIndex);
    }

    /**
     * Returns Iterator for the menu
     * @return
     */
    public Iterator iterator() {
        return contents.iterator();
    }

    /**
     * Removes an item from this menu.
     * @param menuItem
     * @return true if this menu contained the specified item
     */
    public boolean removeMenuItem(Object menuItem) {
        return contents.remove(menuItem);
    }

    /**
     * Returns true if this menu contains the specified item. More formally, returns true if
     * and only if this menu contains at least one item i such that
     * (menuItem==null ? i==null : menuItem.equals(i)).
     * @param menuItem
     * @return
     */
    public boolean contains(Object menuItem) {
        return contents.contains(menuItem);
    }

    /**
     * Cycles the selection through the menu options
     */
    public void cycleMenu() {
        cycleMenu(false);
    }

    /**
     * Cycles the selection through the menu options
     * @param reverse if set to true, cycles backwards
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
     * Sets the current menu selection to selectedIndex
     * @param index
     */
    public void selectMenuItem(int index) {
        selectedIndex = index;
    }

    /**
     * Perform menu item for sender at selectedIndex.  The menu item must be a Runnable or another actionmenu.
     * Runnables will be run and ActionMenus will be shown.
     * @param sender whoever is activating the menu item
     * @param index selectedIndex of the menu item
     * @return the item performed
     */
    public Object doMenuItem(CommandSender sender, int index) {
        Object selectedItem = contents.get(index);
        if (selectedItem instanceof Runnable) {
            ((Runnable)selectedItem).run();
        }
        return selectedItem;
    }

    /**
     * Performs doMenuItem() on the currently selected menu item.
     * @param sender
     * @return the item performed
     */
    public Object doSelectedMenuItem(CommandSender sender) {
        return doMenuItem(sender, selectedIndex);
    }

    /**
     * Shows the menu to a CommandSender
     * @param sender
     */
    public abstract void showMenu(CommandSender sender);
}
