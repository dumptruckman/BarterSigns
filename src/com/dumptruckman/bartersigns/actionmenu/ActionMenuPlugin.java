package com.dumptruckman.bartersigns.actionmenu;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * @author dumptruckman
 */
public class ActionMenuPlugin extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft.ActionMenu");

    public void onEnable() {
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " enabled.");
    }

    public void onDisable() {
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " disabled.");
    }
}
