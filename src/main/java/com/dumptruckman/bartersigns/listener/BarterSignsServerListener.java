package com.dumptruckman.bartersigns.listener;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.palmergames.bukkit.towny.Towny;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * @author dumptruckman
 */
public class BarterSignsServerListener extends ServerListener {

    private BarterSignsPlugin plugin;

    public BarterSignsServerListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin() instanceof Towny) {
            townyEnabled((Towny)event.getPlugin());
        }
    }

    private void townyEnabled(Towny towny) {
        plugin.towny = towny;
    }
}
