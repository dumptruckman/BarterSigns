package com.dumptruckman.bartersigns.block;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import com.dumptruckman.bartersigns.sign.BarterSign;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

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
        if (!event.getLine(0).equalsIgnoreCase("[Barter]")) return;

        BarterSign barterSign = plugin.signManager.getBarterSignFromBlock(event.getBlock());
        barterSign.clear();
        barterSign.init(event.getPlayer());
        plugin.signAndMessage(event, event.getPlayer(),
                plugin.lang.lang(LanguagePath.SIGN_STOCK_SETUP.getPath(), event.getPlayer().getName()));
    }

    public void onBlockDamage(BlockDamageEvent event) {
        System.out.println("Test");
    }
}
