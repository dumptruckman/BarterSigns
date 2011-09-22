package com.dumptruckman.bartersigns;

/**
 * @author dumptruckman
 */
public class BarterSignsSaveTimer implements Runnable {

    private BarterSignsPlugin plugin;

    public BarterSignsSaveTimer(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        plugin.saveData();
    }
}
