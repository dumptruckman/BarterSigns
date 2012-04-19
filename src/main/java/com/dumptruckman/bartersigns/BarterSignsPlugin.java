package com.dumptruckman.bartersigns;

import com.dumptruckman.bartersigns.config.CommentedYamlConfiguration;
import com.dumptruckman.bartersigns.listener.BarterSignsBlockListener;
import com.dumptruckman.bartersigns.listener.BarterSignsEntityListener;
import com.dumptruckman.bartersigns.listener.BarterSignsPlayerListener;
import com.dumptruckman.bartersigns.locale.Language;
import com.dumptruckman.bartersigns.sign.BarterSignManager;
import com.palmergames.bukkit.towny.Towny;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import static com.dumptruckman.bartersigns.config.ConfigPath.*;

/**
 * @author dumptruckman
 */
public class BarterSignsPlugin extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft.BarterSigns");

    public CommentedYamlConfiguration config;
    public CommentedYamlConfiguration data;
    private FileConfiguration items;
    public Language lang;
    public BarterSignManager signManager;
    public Towny towny = null;
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
                (long) (config.getConfig().getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault()) * 20),
                (long) (config.getConfig().getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault()) * 20));

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
        File langFile = new File(this.getDataFolder(), config.getConfig().getString(LANGUAGE_FILE.getPath()));
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
        items = YamlConfiguration.loadConfiguration(itemFile);

        // Check for Towny
        try {
            towny = (Towny)pm.getPlugin("Towny");
        } catch (Exception ignore) {}

        // Register command executor for main plugin command

        // Register event listeners
        pm.registerEvents(new BarterSignsBlockListener(this), this);
        pm.registerEvents(new BarterSignsPlayerListener(this), this);
        pm.registerEvents(new BarterSignsEntityListener(this), this);

        signManager = new BarterSignManager(this);
        
        // Display enable message/version info
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " enabled.");
    }

    final public void onDisable() {
        getServer().getScheduler().cancelTask(saveTaskId);
        saveData();
        log.info(this.getDescription().getName() + " " + getDescription().getVersion() + " disabled.");
    }

    final public void reload(boolean announce) {
        config = new CommentedYamlConfiguration(new File(getDataFolder(), "config.yml"), true);
        config.load();
        data = new CommentedYamlConfiguration(new File(this.getDataFolder(), "data.yml"), false);
        data.load();

        config.getConfig().options().header("# You must reload the server for changes here to take effect");
        config.addComment("settings.languagefile", Arrays.asList("# This is where you specify the language file you wish to use."));
        if (config.getConfig().getString("settings.languagefile") == null) {
            config.getConfig().set("settings.languagefile", "english.yml");
        }
        config.addComment(DATA_SAVE.getPath(), Arrays.asList("# This is how often (in seconds) user data is saved."));
        config.getConfig().getInt(DATA_SAVE.getPath(), (Integer) DATA_SAVE.getDefault());
        config.addComment(USE_PERMS.getPath(), Arrays.asList("# This will enable/disable use of SuperPerms.",
                "# If disabled, all users may create/use signs and OPs will be able to manage every sign."));
        config.getConfig().getBoolean(USE_PERMS.getPath(), (Boolean) USE_PERMS.getDefault());
        config.addComment(SIGN_STORAGE_LIMIT.getPath(), Arrays.asList("# This is the total amount of stock the sign can hold"));
        config.getConfig().getInt(SIGN_STORAGE_LIMIT.getPath(), (Integer) SIGN_STORAGE_LIMIT.getDefault());
        config.addComment(SIGN_ENFORCE_MAX_STACK_SIZE.getPath(), Arrays.asList("# This will make all items dispensed by the sign obey max stack size."));
        config.getConfig().getBoolean(SIGN_ENFORCE_MAX_STACK_SIZE.getPath(), (Boolean) SIGN_ENFORCE_MAX_STACK_SIZE.getDefault());
        config.addComment(SIGN_USE_NUM_STACKS.getPath(), Arrays.asList("# This will cause the stock limit to be in number of stacks."));
        config.getConfig().getBoolean(SIGN_USE_NUM_STACKS.getPath(), (Boolean) SIGN_USE_NUM_STACKS.getDefault());
        config.addComment(SIGN_INDESTRUCTIBLE.getPath(), Arrays.asList("# This will make the signs completely indestructible except by owners/admin"));
        config.getConfig().getBoolean(SIGN_INDESTRUCTIBLE.getPath(), (Boolean) SIGN_INDESTRUCTIBLE.getDefault());
        config.addComment(SIGN_DROPS_ITEMS.getPath(), Arrays.asList("# This will cause the sign to drop all items it contains upon breaking"));
        config.getConfig().getBoolean(SIGN_DROPS_ITEMS.getPath(), (Boolean) SIGN_DROPS_ITEMS.getDefault());

        config.addComment(PLUGINS_OVERRIDE.getPath(), Arrays.asList("# This will cause BarterSigns signs to work regardless of other plugins and may cancel the effect of those plugins.", "# Please keep in mind this is ONLY for signs IN USE by BarterSigns."));
        config.getConfig().getBoolean(PLUGINS_OVERRIDE.getPath(), (Boolean) PLUGINS_OVERRIDE.getDefault());
        config.addComment(TOWNY_SHOP_PLOTS.getPath(), Arrays.asList("# If Towny is in use, this will make it so BarterSigns may only be placed in a Towny Shop Plot."));
        config.getConfig().getBoolean(TOWNY_SHOP_PLOTS.getPath(), (Boolean) TOWNY_SHOP_PLOTS.getDefault());

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
        config.save();
    }

    final public void saveData() {
        data.save();
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
            name = Integer.toString(item.getTypeId());
            if (item.getDurability() > 0) {
                name += "," + item.getDurability();
            }
            log.warning("Missing name for item: '" + name + "' in items.yml");
        }
        return name;
    }

    public boolean enforceMaxStackSize() {
        return config.getConfig().getBoolean(SIGN_ENFORCE_MAX_STACK_SIZE.getPath(), (Boolean) SIGN_ENFORCE_MAX_STACK_SIZE.getDefault());
    }

    public boolean stockLimitUsesStacks() {
        return config.getConfig().getBoolean(SIGN_USE_NUM_STACKS.getPath(), (Boolean) SIGN_USE_NUM_STACKS.getDefault());
    }
}
