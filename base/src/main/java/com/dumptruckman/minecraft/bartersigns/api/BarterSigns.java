package com.dumptruckman.minecraft.bartersigns.api;

import com.dumptruckman.minecraft.pluginbase.plugin.PluginBase;

public interface BarterSigns extends PluginBase<BSConfig> {

    SignManager getSignManager();
}
