package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.locale.LanguagePath;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;

import java.security.SignatureSpi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dumptruckman
 */
public class BarterSignManager {

    private static final int SIGN_MENU_DURATION = 10;

    public static final int BARTER_SIGN = 0;
    public static final int REFRESH_TASK = 1;
    public static final int REFRESH_TASK_ID = 2;

    private static Map<String, List<Object>> activeSigns = new HashMap<String, List<Object>>();
    private static BarterSignsPlugin plugin;

    public BarterSignManager(BarterSignsPlugin plugin) {
        this.plugin = plugin;
        updateSigns();
    }

    final public void updateSigns() {
        List<String> keys = plugin.data.getKeys("signs");
        if (keys == null) return;
        for (String world : keys) {
            for (String loc : plugin.data.getKeys("signs." + world)) {
                String[] locArray = loc.split(",");
                int x = Integer.valueOf(locArray[0]);
                int y = Integer.valueOf(locArray[1]);
                int z = Integer.valueOf(locArray[2]);
                Block block = plugin.getServer().getWorld(world).getBlockAt(x, y, z);
                BarterSign barterSign = new BarterSign(plugin, block);
                if (block.getState() instanceof Sign) {
                    add(barterSign);
                    barterSign.initItems();
                    if (BarterSign.SignPhase.READY.equalTo(barterSign.getPhase())) {
                        barterSign.setupMenu();
                        barterSign.showMenu(null);
                    } else {
                        plugin.signAndMessage(barterSign.getSign(), null, LanguagePath.SIGN_STOCK_SETUP.getPath(),
                                barterSign.getOwner());
                    }
                } else {
                    barterSign.removeFromData();
                }
            }
        }
    }

    public static BarterSign getBarterSignFromBlock(Block block) {
        String name = BarterSign.genName(block);
        if (plugin.data.getNode(name) != null) {
            List<Object> signData = activeSigns.get(name);
            if (signData != null) {
                return (BarterSign) signData.get(BARTER_SIGN);
            }
        }
        return null;
    }

    public static void add(BarterSign barterSign) {
        List<Object> signData = new ArrayList<Object>();
        signData.add(barterSign);
        signData.add(new SignRefreshTask(plugin, barterSign));
        signData.add(-1);
        activeSigns.put(barterSign.getName(), signData);
    }

    public static void remove(Block block) {
        if (BarterSign.exists(plugin, block)) {
            System.out.println(block.toString());
            plugin.data.removeProperty(BarterSign.genName(block));
            cancelSignRefresh(BarterSign.genName(block));
            activeSigns.remove(block.toString());
        }
    }

    public static void cancelSignRefresh(String name) {
        int taskId = (Integer) activeSigns.get(name).get(REFRESH_TASK_ID);
        if (taskId != -1) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }

    public static void scheduleSignRefresh(String name) {
        cancelSignRefresh(name);

        int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                (SignRefreshTask) activeSigns.get(name).get(REFRESH_TASK), (long) (SIGN_MENU_DURATION * 20));
        activeSigns.get(name).set(REFRESH_TASK_ID, taskId);
    }
}
