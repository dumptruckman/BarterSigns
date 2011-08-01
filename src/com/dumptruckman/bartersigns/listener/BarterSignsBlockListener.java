package com.dumptruckman.bartersigns.listener;

import com.avaje.ebeaninternal.server.subclass.MethodWriteReplace;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.config.ConfigPath;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import com.dumptruckman.bartersigns.sign.BarterSignManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.material.Redstone;

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
        BarterSignManager.remove(event.getBlock());
        if (!event.getLine(0).equalsIgnoreCase("[Barter]")) return;
        if (plugin.config.getBoolean(ConfigPath.USE_PERMS.getPath(), (Boolean)ConfigPath.USE_PERMS.getDefault())
                &&!event.getPlayer().hasPermission("bartersigns.create")) return;

        BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
        if (barterSign == null) {
            barterSign = new BarterSign(plugin, event.getBlock());
        }
        barterSign.init(event.getPlayer());
        plugin.signAndMessage(event, event.getPlayer(),
                plugin.lang.lang(LanguagePath.SIGN_STOCK_SETUP.getPath(), event.getPlayer().getName()));
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlockAgainst();
        if (!(block.getState() instanceof Sign)) return;
        if (!BarterSign.exists(plugin, block)) return;
        event.setCancelled(true);
    }

    @Override
    public void onBlockBreak(final BlockBreakEvent event) {
        //if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        BarterSign sign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
        if (sign == null) {
            sign = new BarterSign(plugin, block);
        }
        final BarterSign barterSign = sign;
        
        if (barterSign.getMenuIndex() != barterSign.REMOVE) {
            event.setCancelled(true);
        }

        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            barterSign.drop();
        }

        if (!event.isCancelled()) {
            if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().sendBlockChange(barterSign.getLocation(), 0, (byte)0);
                        barterSign.showMenu(null);
                    }
                }, 300L);
            } else {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().sendBlockChange(barterSign.getLocation(), 0, (byte)0);
                        plugin.signAndMessage(barterSign.getSign(), event.getPlayer(),
                                LanguagePath.SIGN_STOCK_SETUP.getPath(), barterSign.getOwner());
                        barterSign.getBlock().getState().update(true);
                    }
                }, 300L);
            }
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        //if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
        if (barterSign == null) {
            barterSign = new BarterSign(plugin, block);
        }
        if (!BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())
                || barterSign.getMenuIndex() != barterSign.REMOVE) {
            event.setCancelled(true);
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
            BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
            if (barterSign == null) {
                return;
            }
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
            BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
            if (barterSign == null) {
                return;
            }
        }
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        if (!BarterSign.exists(plugin, block)) return;

        event.setCancelled(true);

        if (!event.isCancelled() && plugin.config.getBoolean(ConfigPath.SIGN_DROPS_ITEMS.getPath(),
                (Boolean)ConfigPath.SIGN_DROPS_ITEMS.getDefault())) {
            BarterSign barterSign = BarterSignManager.getBarterSignFromBlock(event.getBlock());
            if (barterSign == null) {
                return;
            }
        }
    }
}
