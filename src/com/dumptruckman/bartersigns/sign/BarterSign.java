package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.actionmenu.ActionMenu;
import com.dumptruckman.actionmenu.ActionMenuItem;
import com.dumptruckman.actionmenu.SignActionMenu;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.menu.AddStockMenuItem;
import com.dumptruckman.bartersigns.menu.CollectRevenueMenuItem;
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
    private static final int REVENUE_COLLECT_MENU = 3;

    private BarterSignsPlugin plugin;

    private Block block;
    private String name;
    private World world;
    private SignActionMenu menu;

    private ItemStack sellableItem;
    private ItemStack acceptableItem;

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

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof BarterSign)) return false;
        return ((BarterSign)obj).getName().equals(this.getName());
    }

    @Override public String toString() {
        return getName();
    }

    @Override public int hashCode() {
        return getName().hashCode();
    }

    public boolean exists() {
        if (plugin.data.getNode(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        if (this.exists()) plugin.data.removeProperty(name);
        plugin.signManager.remove(this);
    }

    public void init(Player player) {
        plugin.data.setProperty(name + ".owner", player.getName());
        setPhase(SignPhase.SETUP_STOCK);
        setupMenu();
        plugin.signManager.add(this);
    }

    public void setupMenu() {
        menu = new SignActionMenu(getBlock());
        menu.addMenuItem(new DefaultMenuItem(plugin.lang.lang(SIGN_READY_SIGN.getPath(), getOwner())));
        menu.addMenuItem(new AddStockMenuItem(plugin, this));
        menu.addMenuItem(new RemoveStockMenuItem(plugin, this));
        menu.addMenuItem(new CollectRevenueMenuItem(plugin, this));
    }

    public void cycleMenu() {
        menu.cycleMenu();
        if (menu.getMenuIndex() != 0) {
            plugin.signManager.scheduleSignRefresh(this);
        }
        menu.getSelectedMenuItem().update();
        
        if (menu.getMenuIndex() != 0) {
            // @TODO Start timer to reset menu
        } else {
        
        }
    }

    public void doSelectedMenuItem(Player player) {
        if (menu.getMenuIndex() != 0) {
            plugin.signManager.scheduleSignRefresh(this);
        }
        menu.doSelectedMenuItem(player);
    }

    public void setMenuIndex(int index) {
        if (index >= menu.size()) {
            index = menu.size() - 1;
        }
        menu.setMenuIndex(index);
        if (menu.getMenuIndex() != 0) {
            plugin.signManager.scheduleSignRefresh(this);
        }
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
        menu.setMenuIndex(READY_MENU);
        showMenu(null);
    }

    public String getOwner() {
        return plugin.data.getString(name + ".owner");
    }

    public void buy(Player player) {
        if (getStock() >= getSellableItem().getAmount()) {
            if (InventoryTools.remove(player.getInventory(), getAcceptableItem().getType(),
                    getAcceptableItem().getDurability(), getAcceptableItem().getAmount())) {
                player.getInventory().addItem(getSellableItem());
                setStock(getStock() - getSellableItem().getAmount());
                setRevenue(getAcceptableItem(), getRevenue(getAcceptableItem()) + getAcceptableItem().getAmount());
            } else {
                String item = getAcceptableItem().getType().toString();
                if (getAcceptableItem().getDurability() > 0) {
                    item += "(" + getAcceptableItem().getDurability() + ")";
                }
                plugin.sendMessage(player, PLAYER_INSUFFICIENT_AMOUNT.getPath(), item);
            }
        } else {
            plugin.sendMessage(player, SIGN_INSUFFICIENT_STOCK.getPath());
        }
    }

    public void updateAllMenuItems() {
        for (ActionMenuItem item : menu) {
            item.update();
        }
    }

    public void updateMenuItem(int index) {
        menu.getMenuItem(index).update();
    }

    public void setSellableItem(Player player, ItemStack item) {
        String itemString = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        plugin.data.setProperty(name + ".sells", itemString);
        String itemName = item.getType().toString();
        if (item.getDurability() != 0) {
            itemName += "(" + item.getDurability() + ")";
        }
        initSellableItem();
        updateMenuItem(ADD_STOCK_MENU);
        updateMenuItem(REMOVE_STOCK_MENU);
        plugin.sendMessage(player, SIGN_STOCK_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public void setAcceptableItem(Player player, ItemStack item) {
        String itemString = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        plugin.data.setProperty(name + ".accepts", itemString);
        String itemName = item.getType().toString();
        if (item.getDurability() != 0) {
            itemName += "(" + item.getDurability() + ")";
        }
        initAcceptableItem();
        plugin.sendMessage(player, SIGN_PAYMENT_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public ItemStack getSellableItem() {
        return sellableItem;
    }

    private void initSellableItem() {
        String item = plugin.data.getString(name + ".sells");
        if (item == null) return;
        String[] sellInfo = item.split("\\s");
        String[] itemData = sellInfo[1].split(":");
        sellableItem = new ItemStack(Integer.valueOf(itemData[0]), Integer.valueOf(sellInfo[0]), Short.valueOf(itemData[1]));
    }

    public ItemStack getAcceptableItem() {
        return acceptableItem;
    }

    private void initAcceptableItem() {
        String item = plugin.data.getString(name + ".accepts");
        if (item == null) return;
        String[] buyInfo = item.split("\\s");
        String[] itemData = buyInfo[1].split(":");
        acceptableItem = new ItemStack(Integer.valueOf(itemData[0]), Integer.valueOf(buyInfo[0]), Short.valueOf(itemData[1]));
    }

    public void initItems() {
        if (SignPhase.READY.equalTo(getPhase())) {
            initSellableItem();
            initAcceptableItem();
        }
    }

    public Integer getStock() {
        return plugin.data.getInt(name + ".stock", 0);
    }

    public void setStock(int stock) {
        plugin.data.setProperty(name + ".stock", stock);
    }

    public Integer getRevenue(ItemStack item) {
        String dataKey = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        return plugin.data.getInt(name + ".revenue." + dataKey, 0);
    }

    public void setRevenue(ItemStack item, int revenue) {
        String dataKey = item.getAmount() + " " + item.getTypeId() + ":" + item.getDurability();
        plugin.data.setProperty(name + ".revenue." + dataKey, revenue);
    }
}
