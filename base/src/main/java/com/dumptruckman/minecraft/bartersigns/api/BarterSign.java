package com.dumptruckman.minecraft.bartersigns.api;

import com.dumptruckman.minecraft.actionmenu2.api.Menu;
import com.dumptruckman.minecraft.pluginbase.config.Config;
import com.dumptruckman.minecraft.pluginbase.config.ConfigEntry;
import com.dumptruckman.minecraft.pluginbase.config.EntryBuilder;
import com.dumptruckman.minecraft.pluginbase.util.BlockLocation;

public interface BarterSign extends Config {

    ConfigEntry<String> OWNER = new EntryBuilder<String>(String.class, "owner").build();

    Menu getMenu();

    BlockLocation getLocation();


}
