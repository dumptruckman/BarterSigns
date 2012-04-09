package com.dumptruckman.minecraft.bartersigns.api;

import com.dumptruckman.minecraft.pluginbase.util.BlockLocation;

public interface SignManager {

    BarterSign getSignAt(BlockLocation loc, boolean loadSign) throws IllegalArgumentException;
}
