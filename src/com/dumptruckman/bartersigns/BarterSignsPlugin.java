package com.dumptruckman.bartersigns;

import com.dumptruckman.bartersigns.block.BarterSignsBlockListener;
import com.dumptruckman.bartersigns.entity.BarterSignsEntityListener;
import com.dumptruckman.bartersigns.entity.player.BarterSignsPlayerListener;
import com.dumptruckman.util.locale.Language;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.Timer;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import static org.bukkit.event.Event.*;

/**
 * @author dumptruckman
 */
public class BarterSignsPlugin extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft.dChest");

    private final BarterSignsBlockListener blockListener = new BarterSignsBlockListener(this);

    public Configuration config;
    public Configuration data;

    private Language lang;
    private Timer timer;

    final public void onEnable() {
        // Grab the PluginManager
        final PluginManager pm = getServer().getPluginManager();

        // Make the data folders that dChest uses
        getDataFolder().mkdirs();

        // Loads the configuration file
        reload(false);

        // Start save timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new BarterSignsSaveTimer(this),
                config.getInt("settings.datasavetimer", 300) * 1000,
                config.getInt("settings.datasavetimer", 300) * 1000);

        // Extracts default english language file
        JarFile jar = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            jar = new JarFile(BarterSignsPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            ZipEntry entry = jar.getEntry("english.yml");
            File efile = new File(getDataFolder(), entry.getName());

            in = new BufferedInputStream(jar.getInputStream(entry));
            out = new BufferedOutputStream(new FileOutputStream(efile));
            byte[] buffer = new byte[2048];
            for (;;)  {
                int nBytes = in.read(buffer);
                if (nBytes <= 0) break;
                out.write(buffer, 0, nBytes);
            }
            out.flush();
        } catch (IOException e) {
            log.warning("Could not extract default language file!");
            if (config.getString("settings.languagefile")
                    .equalsIgnoreCase("english.yml")) {
                log.severe("No alternate language file set!  Disabling "
                        + this.getDescription().getName());
                pm.disablePlugin(this);
                return;
            }
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {}
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }

        // Load up language file
        lang = new Language(new File(this.getDataFolder(), config.getString("settings.languagefile")));

        // Register command executor for main plugin command
        //getCommand("dchest").setExecutor(new DChestPluginCommand(this));

        // Register event listeners
        pm.registerEvent(Type.PLAYER_INTERACT, new BarterSignsPlayerListener(this), Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_FADE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, new BarterSignsEntityListener(this), Priority.Highest, this);

        // Display enable message/version info
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " enabled.");
    }

    final public void onDisable() {
        
    }

    final public void reload(boolean announce) {
        
    }
}
