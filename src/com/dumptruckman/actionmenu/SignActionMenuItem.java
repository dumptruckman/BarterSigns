package com.dumptruckman.actionmenu;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author dumptruckman
 */
public abstract class SignActionMenuItem extends ActionMenuItem {

    protected List<String> lines;
    protected Player player;

    public SignActionMenuItem(List<String> lines) {
        this.lines = lines;
    }

    public void setInteractingPlayer(Player player) {
        this.player = player;
    }

    public List<String> getLines() {
        return lines;
    }

    @Override
    public void onCycle(CommandSender sender) {
        setInteractingPlayer((sender instanceof Player ? (Player) sender : null));
    }

    @Override
    public void onSelect(CommandSender sender) {
        setInteractingPlayer((sender instanceof Player ? (Player) sender : null));
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
