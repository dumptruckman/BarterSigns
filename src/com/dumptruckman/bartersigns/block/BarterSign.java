package com.dumptruckman.bartersigns.block;

import com.dumptruckman.actionmenu.ActionMenu;
import com.dumptruckman.actionmenu.SignActionMenu;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author dumptruckman
 */
public class BarterSign {

    private BarterSignsPlugin plugin;

    private Block block;
    private String name;
    private World world;
    private ActionMenu menu;

    public BarterSign(BarterSignsPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.world = block.getWorld();
        name = "'" + block.getWorld().getName() + "'.'" + block.getX() + "," + block.getY() + "," + block.getZ() + "'";
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public Sign getSign() {
        try {
			BlockState sign = this.world.getBlockAt(block.getX(), block.getY(), block.getZ()).getState();
			if (sign instanceof Sign) return (Sign)sign;
			return null;
		} catch (Exception e) {}
		return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BarterSign)) return false;
        if (((BarterSign)obj).getName().equals(this.getName())) return true;
        return false;
    }

    public boolean exists() {
        return plugin.data.getNode(name) != null;
    }

    public void clear() {
        if (this.exists()) plugin.data.removeProperty(name);
        menu = new SignActionMenu("Barter Sign", block);
    }

    public void init(Player player) {
        plugin.data.setProperty(name + ".owner", player.getName());
    }
}
