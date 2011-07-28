package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.bartersigns.BarterSignsPlugin;

/**
 * @author dumptruckman
 */
public class SignRefreshTask implements Runnable {

    BarterSignsPlugin plugin;
    BarterSign barterSign;

    public SignRefreshTask(BarterSignsPlugin plugin, BarterSign barterSign) {
        this.plugin = plugin;
        this.barterSign = barterSign;
    }

    public void run() {
        barterSign.resumeReadyPhase();
    }
}
