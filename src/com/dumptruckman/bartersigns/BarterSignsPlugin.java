package com.dumptruckman.bartersigns;

import com.dumptruckman.bartersigns.block.BarterSign;
import com.dumptruckman.bartersigns.block.BarterSignsBlockListener;
import com.dumptruckman.bartersigns.entity.BarterSignsEntityListener;
import com.dumptruckman.bartersigns.entity.player.BarterSignsPlayerListener;
import com.dumptruckman.util.io.ConfigIO;
import com.dumptruckman.util.locale.Language;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import static org.bukkit.event.Event.Priority;
import static org.bukkit.event.Event.Type;

/**
 * @author dumptruckman
 */
public class BarterSignsPlugin extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft.dChest");

    private final BarterSignsBlockListener blockListener = new BarterSignsBlockListener(this);

    public Configuration config;
    public Configuration data;
    public Language lang;
    public List<BarterSign> activeSigns = new ArrayList<BarterSign>();

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
                config.getInt("settings.datasavetimer", 30) * 1000,
                config.getInt("settings.datasavetimer", 30) * 1000);

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
        

        // Register event listeners
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, new BarterSignsPlayerListener(this), Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_FADE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.REDSTONE_CHANGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, new BarterSignsEntityListener(this), Priority.Highest, this);

        // Display enable message/version info
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " enabled.");

        updateSigns();
    }

    final public void onDisable() {
        timer.cancel();
        saveFiles();
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " disabled.");
    }

    final public void reload(boolean announce) {
        config = new ConfigIO(new File(this.getDataFolder(), "config.yml")).load();
        data = new ConfigIO(new File(this.getDataFolder(), "data.yml")).load();

        if (config.getString("settings.languagefile") == null) {
            config.setProperty("settings.languagefile", "english.yml");
        }
    }

    final public void saveConfig() {
        new ConfigIO(config).save();
    }

    final public void saveData() {
        new ConfigIO(data).save();
    }

    final public void saveFiles() {
        saveConfig();
        saveData();
    }

    final public void updateSigns() {
        for (String world : data.getKeys()) {
            for (String loc : data.getKeys(world)) {
                String[] locArray = loc.split(",");
                int x = Integer.valueOf(locArray[0]);
                int y = Integer.valueOf(locArray[1]);
                int z = Integer.valueOf(locArray[2]);
                Block block  = getServer().getWorld(world).getBlockAt(x, y, z);
                BarterSign barterSign = new BarterSign(this, block);
                if (block.getState() instanceof Sign) {
                    activeSigns.add(barterSign);
                    barterSign.setupMenu();
                } else {
                    barterSign.removeFromData();
                }
            }
        }
    }

    public void signAndMessage(Sign sign, Player player, List<String> message) {
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, message.get(0));
            message.remove(0);
        }
        sign.update();
        if (player != null)
            lang.sendMessage(message, player);
    }

    public void signAndMessage(Sign sign, Player player, String path, String...args) {
        List<String> message = lang.lang(path, args);
        signAndMessage(sign, player, message);
    }

    public void signAndMessage(SignChangeEvent event, Player player, List<String> message) {
        for (int i = 0; i < 4; i++) {
            event.setLine(i, message.get(0));
            message.remove(0);
        }
        lang.sendMessage(message, player);
    }

    public void sendMessage(CommandSender sender, String path, String...args) {
        lang.sendMessage(lang.lang(path, args), sender);
    }
}
