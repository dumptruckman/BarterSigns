package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.actionmenu.SignActionMenu;
import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.menu.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.dumptruckman.bartersigns.locale.LanguagePath.*;

/**
 * @author dumptruckman
 */
public class BarterSign {

    private static final int READY_MENU = 0;

    private BarterSignsPlugin plugin;

    private Block block;
    private String name;
    private World world;
    private SignActionMenu menu;

    private ItemStack sellableItem = null;
    private List<ItemStack> acceptableItems = null;

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
        activateStockPhase(player);
        plugin.signManager.add(this);
    }

    public void setupMenu() {
        menu = new SignActionMenu(getBlock());
        menu.addMenuItem(new DefaultMenuItem(plugin.lang.lang(SIGN_READY_SIGN.getPath(), getOwner())));
        menu.addMenuItem(new CollectRevenueMenuItem(plugin, this));
        menu.addMenuItem(new AddStockMenuItem(plugin, this));
        menu.addMenuItem(new RemoveStockMenuItem(plugin, this));
        menu.addMenuItem(new AddPaymentMenuItem(plugin, this));
        menu.addMenuItem(new RemovePaymentMenuItem(plugin, this));
        menu.addMenuItem(new IncreaseSellableMenuItem(plugin, this));
        menu.addMenuItem(new DecreaseSellableMenuItem(plugin, this));
    }

    public void cycleMenu() {
        menu.updateMenuItems();
        menu.cycleMenu();
        if (menu.getMenuIndex() != 0) {
            plugin.signManager.scheduleSignRefresh(this);
        }
        menu.getSelectedMenuItem().update();
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
            menu.updateMenuItems();
            plugin.signManager.scheduleSignRefresh(this);
        }
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
        setPhase(SignPhase.SETUP_STOCK);
        plugin.signAndMessage(getSign(), player, SIGN_PAYMENT_SETUP.getPath(), player.getName());
    }

    public void activateReadyPhase(Player player) {
        setPhase(SignPhase.READY);
        initAcceptableItems();
        setupMenu();
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
            ItemStack playerItem = player.getItemInHand();
            ItemStack acceptItem = null;
            for (ItemStack item : getAcceptableItems()) {
                if (item.getTypeId() == playerItem.getTypeId() &&
                        item.getDurability() == playerItem.getDurability()) {
                    acceptItem = item;
                    break;
                }
            }
            if (acceptItem != null) {
                if (InventoryTools.remove(player.getInventory(), acceptItem.getType(),
                        acceptItem.getDurability(), acceptItem.getAmount())) {
                    player.getInventory().addItem(getSellableItem());
                    setStock(getStock() - getSellableItem().getAmount());
                    setRevenue(acceptItem, getRevenue(acceptItem) + acceptItem.getAmount());
                } else {
                    plugin.sendMessage(player, PLAYER_INSUFFICIENT_AMOUNT.getPath(),
                            plugin.itemToString(acceptItem, false));
                }
            } else {
                plugin.sendMessage(player, PLAYER_UNACCEPTABLE_ITEM.getPath());
            }
        } else {
            plugin.sendMessage(player, SIGN_INSUFFICIENT_STOCK.getPath());
        }
    }

    public void setSellableItem(Player player, ItemStack item) {
        plugin.data.setProperty(name + ".sells", plugin.itemToDataString(item));
        String itemName = plugin.itemToString(item, false);
        initSellableItem();
        plugin.sendMessage(player, SIGN_STOCK_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public void addAcceptableItem(Player player, ItemStack item) {
        List<String> items = getAcceptableItemsString();
        item.setAmount(1);
        items.add(plugin.itemToDataString(item));
        plugin.data.setProperty(name + ".accepts", items);
        initAcceptableItems();
        plugin.sendMessage(player, SIGN_PAYMENT_ADDED.getPath(), plugin.itemToString(item));
    }

    public void removeAcceptableItem(Player player, ItemStack item) {
        item = getAcceptableItems().get(indexOf(item));
        getAcceptableItems().remove(item);
        List<String> items = getAcceptableItemsString();
        items.remove(plugin.itemToDataString(item));
        plugin.data.setProperty(name + ".accepts", items);
        plugin.sendMessage(player, SIGN_PAYMENT_REMOVED.getPath(), plugin.itemToString(item));
    }

    public int increaseAcceptableItemAmount(ItemStack item) {
        int index = indexOf(item);
        List<String> items = getAcceptableItemsString();
        int stringIndex = items.indexOf(plugin.itemToDataString(item));
        item.setAmount(item.getAmount() + 1);
        acceptableItems.set(index, item);
        items.set(stringIndex, plugin.itemToDataString(item));
        plugin.data.setProperty(name + ".accepts", items);
        return item.getAmount();
    }

    public int decreaseAcceptableItemAmount(ItemStack item) {
        int index = indexOf(item);
        List<String> items = getAcceptableItemsString();
        int stringIndex = items.indexOf(plugin.itemToDataString(item));
        item.setAmount(item.getAmount() - 1);
        acceptableItems.set(index, item);
        items.set(stringIndex, plugin.itemToDataString(item));
        plugin.data.setProperty(name + ".accepts", items);
        return item.getAmount();
    }

    public int increaseSellableItemAmount() {
        sellableItem.setAmount(sellableItem.getAmount() + 1);
        plugin.data.setProperty(name + ".sells", plugin.itemToDataString(sellableItem));
        return sellableItem.getAmount();
    }

    public int decreaseSellableItemAmount() {
        sellableItem.setAmount(sellableItem.getAmount() - 1);
        plugin.data.setProperty(name + ".sells", plugin.itemToDataString(sellableItem));
        return sellableItem.getAmount();
    }

    public ItemStack getSellableItem() {
        return sellableItem;
    }

    public String getSellableItemString() {
        return plugin.data.getString(name + ".sells");
    }

    private void initSellableItem() {
        String item = getSellableItemString();
        if (item == null) return;
        sellableItem = plugin.stringToItem(item);
    }

    public List<ItemStack> getAcceptableItems() {
        return acceptableItems;
    }

    public List<String> getAcceptableItemsString() {
        return plugin.data.getStringList(name + ".accepts", new ArrayList<String>());
    }

    public boolean contains(ItemStack i) {
        for (ItemStack item : getAcceptableItems()) {
            if (i.getType() == item.getType() && (i.getDurability() == item.getDurability())) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(ItemStack s) {
        for (int i = 0; i < getAcceptableItems().size(); i++) {
            ItemStack item = getAcceptableItems().get(i);
            if (s.getType() == item.getType() && (s.getDurability() == item.getDurability())) {
                return i;
            }
        }
        return -1;
    }

    private void initAcceptableItems() {
        acceptableItems = new ArrayList<ItemStack>();
        List<String> items = getAcceptableItemsString();
        if (items == null) return;
        for (Object item : items) {
            acceptableItems.add(plugin.stringToItem(item.toString()));
        }
    }

    public void initItems() {
        if (SignPhase.READY.equalTo(getPhase())) {
            initSellableItem();
            initAcceptableItems();
        }
    }

    public Integer getStock() {
        return plugin.data.getInt(name + ".stock", 0);
    }

    public void setStock(int stock) {
        plugin.data.setProperty(name + ".stock", stock);
    }

    public Integer getRevenue(ItemStack item) {
        return plugin.data.getInt(name + ".revenue." + plugin.itemToDataString(item), 0);
    }

    public void setRevenue(ItemStack item, int revenue) {
        plugin.data.setProperty(name + ".revenue." + plugin.itemToDataString(item), revenue);
    }
}
