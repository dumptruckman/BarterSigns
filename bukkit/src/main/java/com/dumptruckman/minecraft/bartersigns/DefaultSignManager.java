package com.dumptruckman.minecraft.bartersigns;

import com.dumptruckman.minecraft.bartersigns.api.BarterSign;
import com.dumptruckman.minecraft.bartersigns.api.SignManager;
import com.dumptruckman.minecraft.bartersigns.util.Language;
import com.dumptruckman.minecraft.pluginbase.util.BlockLocation;
import com.dumptruckman.minecraft.pluginbase.util.Blocks;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

class DefaultSignManager implements SignManager {

    private final BarterSignsPlugin plugin;
    private final Map<BlockLocation, BarterSign> signMap = new WeakHashMap<BlockLocation, BarterSign>();

    private final File signFolder;
    private final Map<String, File> worldFolders = new WeakHashMap<String, File>();

    public DefaultSignManager(BarterSignsPlugin plugin) {
        this.plugin = plugin;
        signFolder = new File(this.plugin.getDataFolder(), "signs");
    }

    @Override
    public BarterSign getSignAt(BlockLocation loc, boolean loadSign) throws IllegalArgumentException {
        Block block = Blocks.getBlockAt(loc);
        if (block == null || !(block.getState() instanceof Sign)) {
            throw new IllegalArgumentException(plugin.getMessager().getMessage(Language.ERROR_LOCATION_NOT_SIGN, loc));
        }
        BarterSign bSign = signMap.get(loc);
        if (bSign == null && loadSign) {
            Logging.finer("No cached sign, loading from disk...");
            bSign = getSignFromPersistence(loc);
            if (bSign != null) {
                signMap.put(loc, bSign);
                Logging.finest("Cached sign from disk");
            } else {
                Logging.finest("No sign found on disk");
            }
        } else {
            Logging.finest("Got cached sign for location: " + loc.toString());
        }
        return bSign;
    }

    private File getSignFolder() {
        return signFolder;
    }

    private File getWorldFolder(String world) {
        File worldFolder = worldFolders.get(world);
        if (worldFolder == null) {
            worldFolder = new File(getSignFolder(), world);
            if (!worldFolder.exists()) {
                worldFolder.mkdirs();
            }
            worldFolders.put(world, worldFolder);
        }
        return worldFolder;
    }

    private BarterSign getSignFromPersistence(BlockLocation loc) throws IllegalArgumentException {
        File signFile = new File(getWorldFolder(loc.getWorld()), loc.toString() + ".yml");
        if (signFile.exists()) {
            try {
                return new DefaultBarterSign(plugin, signFile, loc);
            } catch (IOException e) {
                Logging.warning("Could not load BarterSign from file: " + signFile.getName());
                Logging.warning("IOException: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}
