package com.dumptruckman.bartersigns.listener;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.config.ConfigPath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.*;

/**
 * @author dumptruckman
 */
public class BarterSignsBlockListener extends BlockListener {

    private BarterSignsPlugin plugin;

    public BarterSignsBlockListener(BarterSignsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onSignChange(SignChangeEvent event) {
        // Throw out unimportant events immediately
        if (event.isCancelled()) return;
        BarterSign.removeIfExists(plugin, event.getBlock());
        if (!event.getLine(0).equalsIgnoreCase("[Barter]")) return;
        if (!event.getPlayer().hasPermission("bartersigns.create")) return;

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(event.getBlock());
        barterSign.clear();
        barterSign.init(event.getPlayer());
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getPlayer().getTargetBlock(null, 5);
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        event.setCancelled(true);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(event.getBlock());
        if (barterSign.getMenuIndex() != barterSign.REMOVE) {
            event.setCancelled(true);
        }

        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            barterSign.drop();
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(event.getBlock());
        if (barterSign.getMenuIndex() != barterSign.REMOVE) {
            event.setCancelled(true);
        }


        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            barterSign.drop();
        }
    }

    @Override
    public void onBlockBurn(BlockBurnEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        event.setCancelled(plugin.config.getBoolean(ConfigPath.SIGN_INDESTRUCTIBLE.getPath(),
                (Boolean)ConfigPath.SIGN_INDESTRUCTIBLE.getDefault()));

        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            plugin.signManager.getBarterSignFromBlock(event.getBlock()).drop();
        }
    }

    @Override
    public void onBlockFade(BlockFadeEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        event.setCancelled(plugin.config.getBoolean(ConfigPath.SIGN_INDESTRUCTIBLE.getPath(),
                (Boolean)ConfigPath.SIGN_INDESTRUCTIBLE.getDefault()));

        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            plugin.signManager.getBarterSignFromBlock(event.getBlock()).drop();
        }
    }
}
