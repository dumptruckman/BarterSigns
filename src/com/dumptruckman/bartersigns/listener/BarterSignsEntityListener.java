package com.dumptruckman.bartersigns.listener;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.config.ConfigPath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import com.sun.org.apache.xpath.internal.operations.And;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dumptruckman
 */
public class BarterSignsEntityListener extends EntityListener {

    private BarterSignsPlugin plugin;

    public BarterSignsEntityListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        List<Block> blocks = event.blockList();
        List<BarterSign> barterSigns = new ArrayList<BarterSign>();
        for (Block block : blocks) {
            if (block.getState() instanceof Sign) {
                if (BarterSign.exists(plugin, block)) {
                    barterSigns.add(plugin.signManager.getBarterSignFromBlock(block));
                    if (plugin.config.getBoolean(ConfigPath.SIGN_INDESTRUCTIBLE.getPath(),
                            (Boolean)ConfigPath.SIGN_INDESTRUCTIBLE.getDefault())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            for (BarterSign sign : barterSigns) {
                sign.drop();
            }
        }
    }
}
