package com.dumptruckman.bartersigns;

import com.dumptruckman.bartersigns.listener.BarterSignsBlockListener;
import com.dumptruckman.bartersigns.listener.BarterSignsEntityListener;
import com.dumptruckman.bartersigns.listener.BarterSignsPlayerListener;
import com.dumptruckman.bartersigns.sign.BarterSignManager;
import com.dumptruckman.bartersigns.config.ConfigIO;
import com.dumptruckman.bartersigns.locale.Language;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import static com.dumptruckman.bartersigns.config.ConfigPath.*;
import static org.bukkit.event.Event.Priority;
import static org.bukkit.event.Event.Type;

/**
 * @author dumptruckman
 */
public class BarterSignsPlugin extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft.BarterSigns");

    private final BarterSignsBlockListener blockListener = new BarterSignsBlockListener(this);

    public Configuration config;
    public Configuration data;
    private Configuration items;
    public Language lang;
    public BarterSignManager signManager;
    private int saveTaskId;

    final public void onEnable() {
        // Grab the PluginManager
        final PluginManager pm = getServer().getPluginManager();

        // Make the data folders that dChest uses
        getDataFolder().mkdirs();

        // Loads the configuration file
        reload(false);

        // Start save timer
        saveTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new BarterSignsSaveTimer(this),
                (long) (config.getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault()) * 20),
                (long) (config.getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault()) * 20));

        //if (config.getString(LANGUAGE_FILE.getPath())
        //        .equalsIgnoreCase(LANGUAGE_FILE.getDefault().toString())) {
            // Extracts default english language file
        extractFromJar("english.yml");
        //}
        File itemFile = new File(this.getDataFolder(), "items.yml");
        if (!itemFile.exists()) {
            extractFromJar("items.yml");
        }

        // Load up language file
        File langFile = new File(this.getDataFolder(), config.getString(LANGUAGE_FILE.getPath()));
        if (!langFile.exists()) {
            log.severe("Language file: " + langFile.getName() + " is missing!  Disabling "
                    + this.getDescription().getName());
            pm.disablePlugin(this);
            return;
        }
        lang = new Language(langFile);

        // Load up item file
        if (!itemFile.exists()) {
            log.severe("items.yml is missing!  Disabling "
                    + this.getDescription().getName());
            pm.disablePlugin(this);
            return;
        }
        items = new ConfigIO(itemFile).load();

        // Register command executor for main plugin command


        // Register event listeners
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, new BarterSignsPlayerListener(this), Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK, new BarterSignsPlayerListener(this), Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_FADE, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, new BarterSignsEntityListener(this), Priority.Highest, this);

        // Display enable message/version info
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " enabled.");

        signManager = new BarterSignManager(this);
    }

    final public void onDisable() {
        getServer().getScheduler().cancelTask(saveTaskId);
        saveData();
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " disabled.");
    }

    final public void reload(boolean announce) {
        config = new ConfigIO(new File(this.getDataFolder(), "config.yml")).load();
        data = new ConfigIO(new File(this.getDataFolder(), "data.yml")).load();

        if (config.getString("settings.languagefile") == null) {
            config.setProperty("settings.languagefile", "english.yml");
        }
        config.getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault());
        config.getBoolean(USE_PERMS.getPath(), (Boolean) USE_PERMS.getDefault());
        config.getInt(SIGN_STORAGE_LIMIT.getPath(), (Integer)SIGN_STORAGE_LIMIT.getDefault());
        config.getBoolean(SIGN_INDESTRUCTIBLE.getPath(), (Boolean) SIGN_INDESTRUCTIBLE.getDefault());
        config.getBoolean(SIGN_DROPS_ITEMS.getPath(), (Boolean) SIGN_DROPS_ITEMS.getDefault());
        config.save();
    }

    private void extractFromJar(String fileName) {
        JarFile jar = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                String path = BarterSignsPlugin.class.getProtectionDomain().getCodeSource()
                        .getLocation().getPath();
                path = path.replaceAll("%20", " ");
                jar = new JarFile(path);
                ZipEntry entry = jar.getEntry(fileName);
                File efile = new File(getDataFolder(), entry.getName());

                in = new BufferedInputStream(jar.getInputStream(entry));
                out = new BufferedOutputStream(new FileOutputStream(efile));
                byte[] buffer = new byte[2048];
                for (; ; ) {
                    int nBytes = in.read(buffer);
                    if (nBytes <= 0) break;
                    out.write(buffer, 0, nBytes);
                }
                out.flush();
                in.close();
                out.close();
            } catch (IOException e) {
                log.warning("Could not extract " + fileName + "! Reason: " + e.getMessage());
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException e) {
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
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

    public void signAndMessage(Sign sign, Player player, List<String> message) {
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, message.get(0));
            message.remove(0);
        }
        if (player != null) {
            lang.sendMessage(message, player);
            player.sendBlockChange(sign.getBlock().getLocation(), 0, (byte) 0);
        }
        sign.update(true);
    }

    public void signAndMessage(Sign sign, Player player, String path, String... args) {
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

    public void sendMessage(CommandSender sender, String path, String... args) {
        lang.sendMessage(lang.lang(path, args), sender);
    }

    public String itemToDataString(ItemStack item) {
        return itemToDataString(item, true);
    }

    public String itemToDataString(ItemStack s, boolean withAmount) {
        String item = "";
        if (withAmount) {
            item += s.getAmount() + " ";
        }
        item += s.getTypeId() + "," + s.getDurability();
        return item;
    }

    public ItemStack stringToItem(String item) {
        String[] sellInfo = item.split("\\s");
        String[] itemData = sellInfo[1].split(",");
        return new ItemStack(Integer.valueOf(itemData[0]), Integer.valueOf(sellInfo[0]), Short.valueOf(itemData[1]));
    }

    public String itemToString(ItemStack item) {
        return itemToString(item, true);
    }

    public String itemToString(ItemStack s, boolean withAmount) {
        String item = "";
        if (withAmount) {
            item += s.getAmount() + " ";
        }
        item += getShortItemName(s);
        return item;
    }

    public String getShortItemName(ItemStack item) {
        String key = itemToDataString(item, false);
        String name = items.getString(key);
        if (name == null) {
            name = items.getString(item.getTypeId() + ",0");
        }
        if (name == null) {
            log.warning("Missing item name in items.yml");
            name = "???";
        }
        return name;
    }
}
