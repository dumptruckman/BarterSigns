package com.dumptruckman.bartersigns.block;

import com.dumptruckman.actionmenu.ActionMenu;
import com.dumptruckman.actionmenu.ActionMenuItem;
import com.dumptruckman.actionmenu.SignActionMenu;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.menu.AddStockMenuItem;
import com.dumptruckman.bartersigns.menu.DefaultMenuItem;
import com.dumptruckman.bartersigns.menu.RemoveStockMenuItem;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.dumptruckman.bartersigns.locale.LanguagePath.*;

/**
 * @author dumptruckman
 */
public class BarterSign {

    private static final int READY_MENU = 0;
    private static final int ADD_STOCK_MENU = 1;
    private static final int REMOVE_STOCK_MENU = 2;

    private BarterSignsPlugin plugin;

    private Block block;
    private String name;
    private World world;
    private SignActionMenu menu;

    public enum SignPhase {
        SETUP_STOCK, SETUP_PAYMENT, READY;

        public boolean equalTo(Object o) {
            return this.toString().equals(o.toString());
        }
    }

    public BarterSign(BarterSignsPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.world = block.getWorld();
        name = block.getWorld().getName() + "." + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public void removeFromData() {
        plugin.data.removeProperty(name);
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public Sign getSign() {
        try {
			BlockState sign = this.world.getBlockAt(block.getX(), block.getY(), block.getZ()).getState();
			if (sign instanceof Sign) return (Sign)sign;
			return null;
		} catch (Exception e) {}
		return null;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BarterSign)) return false;
        if (((BarterSign)obj).getName().equals(this.getName())) return true;
        return false;
    }

    public boolean exists() {
        return plugin.data.getNode(name) != null;
    }

    public void clear() {
        if (this.exists()) plugin.data.removeProperty(name);
        menu = new SignActionMenu("Barter Sign", block);
    }

    public void init(Player player) {
        plugin.data.setProperty(name + ".owner", player.getName());
        setPhase(SignPhase.SETUP_STOCK);
        setupMenu();
    }

    public void setupMenu() {
        menu = new SignActionMenu("Barter Sign", getBlock());
        menu.addMenuItem(new DefaultMenuItem(plugin.lang.lang(SIGN_READY_SIGN.getPath(), getOwner())));
        menu.addMenuItem(new AddStockMenuItem(plugin, this));
        menu.addMenuItem(new RemoveStockMenuItem(plugin, this));
    }

    public ActionMenu getMenu() {
        if (menu == null) setupMenu();
        return menu;
    }

    public void showMenu(Player player) {
        menu.showMenu(player);
    }

    public String getPhase() {
        return plugin.data.getString(name + ".phase");
    }

    public void setPhase(SignPhase phase) {
        plugin.data.setProperty(name + ".phase", phase.toString());
    }

    public void activateStockPhase(Player player) {
        plugin.signAndMessage(getSign(), player, SIGN_PAYMENT_SETUP.getPath(), player.getName());
    }

    public void activatePaymentPhase(Player player) {
        plugin.signAndMessage(getSign(), player, SIGN_PAYMENT_SETUP.getPath(), player.getName());
    }

    public void activateReadyPhase(Player player) {
        resumeReadyPhase();
        plugin.sendMessage(player, SIGN_READY_MESSAGE.getPath());
    }

    public void resumeReadyPhase() {
        menu.selectMenuItem(READY_MENU);
        showMenu(null);
    }

    public String getOwner() {
        return plugin.data.getString(name + ".owner");
    }

    public void updateAllMenuItems() {
        for (ActionMenuItem item : menu) {
            item.update();
        }
    }

    public void updateMenuItem(int index) {
        menu.getMenuItem(index).update();
    }

    public void setStockItem(Player player, ItemStack item) {
        String itemString = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        plugin.data.setProperty(name + ".sells", itemString);
        String itemName = item.getType().toString();
        if (item.getDurability() != 0) {
            itemName += "(" + item.getDurability() + ")";
        }
        updateMenuItem(ADD_STOCK_MENU);
        updateMenuItem(REMOVE_STOCK_MENU);
        plugin.sendMessage(player, SIGN_STOCK_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public void setPaymentItem(Player player, ItemStack item) {
        String itemString = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        plugin.data.setProperty(name + ".accepts", itemString);
        String itemName = item.getType().toString();
        if (item.getDurability() != 0) {
            itemName += "(" + item.getDurability() + ")";
        }
        plugin.sendMessage(player, SIGN_PAYMENT_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public ItemStack getSells() {
        String item = plugin.data.getString(name + ".sells");
        if (item == null) return null;
        String[] sellInfo = item.split("\\s");
        String[] itemData = sellInfo[1].split(":");
        return new ItemStack(Integer.valueOf(itemData[0]), Integer.valueOf(sellInfo[0]), Short.valueOf(itemData[1]));
    }

    public ItemStack getAccepts() {
        String item = plugin.data.getString(name + ".accepts");
        if (item == null) return null;
        String[] buyInfo = item.split("\\s");
        String[] itemData = buyInfo[1].split(":");
        return new ItemStack(Integer.valueOf(itemData[0]), Integer.valueOf(buyInfo[0]), Short.valueOf(itemData[1]));
    }

    public Integer getStock() {
        return plugin.data.getInt(name + ".stock", 0);
    }

    public void setStock(int stock) {
        plugin.data.setProperty(name + ".stock", stock);

    }
}
