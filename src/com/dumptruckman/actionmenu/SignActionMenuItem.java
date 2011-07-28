package com.dumptruckman.actionmenu;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author dumptruckman
 */
public abstract class SignActionMenuItem extends ActionMenuItem {

    protected List<String> lines;

    public SignActionMenuItem(List<String> lines) {
        this.lines = lines;
    }

    protected Player player;

    public void setInteractingPlayer(Player player) {
        this.player = player;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getLine(int index) {
        return lines.get(index);
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        if (index > 3) {
            throw new IndexOutOfBoundsException("Only 4 lines allowed");
        }
        lines.set(index, line);
    }
}
