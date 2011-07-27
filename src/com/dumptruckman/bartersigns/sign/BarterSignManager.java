package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

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

    private Map<BarterSign, List<Object>> activeSigns = new HashMap<BarterSign, List<Object>>();
    private BarterSignsPlugin plugin;

    public BarterSignManager(BarterSignsPlugin plugin) {
        this.plugin = plugin;
        updateSigns();
    }

    final public void updateSigns() {
        for (String world : plugin.data.getKeys()) {
            for (String loc : plugin.data.getKeys(world)) {
                String[] locArray = loc.split(",");
                int x = Integer.valueOf(locArray[0]);
                int y = Integer.valueOf(locArray[1]);
                int z = Integer.valueOf(locArray[2]);
                Block block  = plugin.getServer().getWorld(world).getBlockAt(x, y, z);
                BarterSign barterSign = new BarterSign(plugin, block);
                if (block.getState() instanceof Sign) {
                    add(barterSign);
                    barterSign.setupMenu();
                    barterSign.showMenu(null);
                } else {
                    barterSign.removeFromData();
                }
            }
        }
    }

    public BarterSign getBarterSignFromBlock(Block block) {
        BarterSign barterSign = new BarterSign(plugin, block);
        if (barterSign.exists()) {
            List<Object> signData = activeSigns.get(barterSign);
            if (signData != null) {
                barterSign = (BarterSign)signData.get(BARTER_SIGN);
            }
        }
        return barterSign;
    }

    public void add(BarterSign barterSign) {
        List<Object> signData = new ArrayList<Object>();
        signData.add(barterSign);
        signData.add(new SignRefreshTask(plugin, barterSign));
        signData.add(-1);
        activeSigns.put(barterSign, signData);
    }

    public void remove(BarterSign barterSign) {
        activeSigns.remove(barterSign);
    }

    public void scheduleSignRefresh(BarterSign barterSign) {
        int taskId = (Integer)activeSigns.get(barterSign).get(REFRESH_TASK_ID);
        if (taskId != -1) {
            System.out.println("hmm");
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
        taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                (SignRefreshTask)activeSigns.get(barterSign).get(REFRESH_TASK), (long)(SIGN_MENU_DURATION * 20));
        activeSigns.get(barterSign).set(REFRESH_TASK_ID, taskId);
    }
}
