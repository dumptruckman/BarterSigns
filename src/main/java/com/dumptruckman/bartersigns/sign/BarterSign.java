package com.dumptruckman.bartersigns.sign;

import com.dumptruckman.bartersigns.BarterSignsPlugin;
import com.dumptruckman.bartersigns.actionmenu.SignActionMenu;
import com.dumptruckman.bartersigns.inventory.InventoryTools;
import com.dumptruckman.bartersigns.menu.AlterPaymentMenuItem;
import com.dumptruckman.bartersigns.menu.AlterSellableMenuItem;
import com.dumptruckman.bartersigns.menu.AlterStockMenuItem;
import com.dumptruckman.bartersigns.menu.CollectRevenueMenuItem;
import com.dumptruckman.bartersigns.menu.HelpMenuItem;
import com.dumptruckman.bartersigns.menu.MainMenuItem;
import com.dumptruckman.bartersigns.menu.RemoveSignMenuItem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dumptruckman.bartersigns.locale.LanguagePath.*;

/**
 * @author dumptruckman
 */
public class BarterSign {

    private static final int READY_MENU = 0;

    public int REMOVE = -1;

    private BarterSignsPlugin plugin;

    private Block block;
    private String name;
    private World world;
    private SignActionMenu menu;
    private String network;
    private Location location;

    private ItemStack sellableItem = null;
    private List<ItemStack> acceptableItems = null;

    public enum SignPhase {
        SETUP_STOCK, READY;

        public boolean equalTo(Object o) {
            return this.toString().equals(o.toString());
        }
    }

    public BarterSign(BarterSignsPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
        this.world = block.getWorld();
        name = genName(block);
    }

    public static String genName(Block block) {
        return "signs." + block.getWorld().getName() + "." + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static boolean exists(BarterSignsPlugin plugin, Block block) {
        return (plugin.data.getConfig().get(genName(block)) != null);
    }

    public void drop() {
        Location loc = block.getLocation();
        if (getStock() != 0)
            world.dropItemNaturally(loc, new ItemStack(sellableItem.getType(), getStock(),
                    sellableItem.getDurability()));
        for (ItemStack item : acceptableItems) {
            int revenue = getRevenue(item);
            if (revenue != 0)
                world.dropItemNaturally(loc, new ItemStack(item.getType(), revenue, item.getDurability()));
        }
        BarterSignManager.remove(getBlock());
    }

    public Integer getMenuIndex() {
        return menu.getMenuIndex();
    }

    public void removeFromData() {
        plugin.data.getConfig().set(name, null);
    }

    public String getName() {
        return name;
    }

    public String getNetwork() {
        return plugin.config.getConfig().getString(name + ".network");
    }

    public void removeNetwork() {
        plugin.config.getConfig().set(name + ".network", null);
    }

    public Block getBlock() {
        return block;
    }

    public Sign getSign() {
        try {
            BlockState sign = this.world.getBlockAt(block.getX(), block.getY(), block.getZ()).getState();
            if (sign instanceof Sign) return (Sign) sign;
            return null;
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public boolean exists() {
        return (plugin.data.getConfig().get(name) != null);
    }

    public void init(Player player) {
        this.location = block.getLocation();
        plugin.data.getConfig().set(name + ".owner", player.getName());
        activateStockPhase(player);
        BarterSignManager.add(this);
    }

    public void setupMenu() {
        menu = new SignActionMenu(getBlock());
        menu.addMenuItem(new MainMenuItem(plugin, this, plugin.lang.lang(SIGN_READY_SIGN.getPath(),
                Integer.toString(getSellableItem().getAmount()), plugin.getShortItemName(getSellableItem()))));
        menu.addMenuItem(new HelpMenuItem(plugin, this));
        menu.addMenuItem(new AlterStockMenuItem(plugin, this));
        menu.addMenuItem(new AlterPaymentMenuItem(plugin, this));
        menu.addMenuItem(new AlterSellableMenuItem(plugin, this));
        REMOVE = menu.addMenuItem(new RemoveSignMenuItem(plugin, this));
        menu.addMenuItem(new CollectRevenueMenuItem(plugin, this));
    }

    public void cycleMenu(Player player, boolean reverse) {
        menu.cycleMenu(player, reverse);
        if (menu.getMenuIndex() != 0) {
            BarterSignManager.scheduleSignRefresh(this.getName());
        }
    }

    public void doSelectedMenuItem(Player player) {
        if (menu.getMenuIndex() != 0) {
            BarterSignManager.scheduleSignRefresh(this.getName());
        }
        menu.doSelectedMenuItem(player);
    }

    public void setMenuIndex(Player player, int index) {
        if (index >= menu.getContents().size()) {
            index = menu.getContents().size() - 1;
        }
        menu.setMenuIndex(player, index);
        if (menu.getMenuIndex() != 0) {
            BarterSignManager.scheduleSignRefresh(this.getName());
        }
    }

    public void showMenu(Player player) {
        if (block.getState() instanceof Sign) {
            menu.showMenu(player);
        } else {
            BarterSignManager.remove(getBlock());
        }
    }

    public World getWorld() {
        return world;
    }

    //public Location getLocation() {
    //    return block.getLocation();
    //}

    public void showInfo(Player player) {
        List<ItemStack> acceptItems = getAcceptableItems();
        String acceptItemsString = "";
        for (int i = 0; i < acceptItems.size(); i++) {
            if (i > 0) {
                acceptItemsString += " OR ";
            }
            acceptItemsString += plugin.itemToString(acceptItems.get(i));
        }
        plugin.sendMessage(player, SIGN_INFO.getPath(), getOwner(), acceptItemsString);
    }

    public String getPhase() {
        return plugin.data.getConfig().getString(name + ".phase");
    }

    public boolean isReady() {
        return SignPhase.READY.equalTo(getPhase());
    }

    public void setPhase(SignPhase phase) {
        plugin.data.getConfig().set(name + ".phase", phase.toString());
    }

    public void activateStockPhase(Player player) {
        setPhase(SignPhase.SETUP_STOCK);
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
        return plugin.data.getConfig().getString(name + ".owner");
    }

    public Location getLocation() {
        return location;
    }

    public void spawnItemsAtSign(HashMap<Integer, ItemStack> items) {
        if (items != null && !items.isEmpty()) {
            for (Map.Entry<Integer, ItemStack> item : items.entrySet()) {
                getBlock().getWorld().dropItem(getLocation(), item.getValue());
            }
        }
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

                    HashMap<Integer, ItemStack> leftover = null;
                    if (plugin.enforceMaxStackSize()) {
                        List<ItemStack> items = InventoryTools.getSeparatedItems(getSellableItem());
                        leftover = player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                    } else {
                        leftover = player.getInventory().addItem(new ItemStack(getSellableItem().getType(),
                                getSellableItem().getAmount(), getSellableItem().getDurability()));
                    }
                    spawnItemsAtSign(leftover);
                    setStock(getStock() - getSellableItem().getAmount());
                    setRevenue(acceptItem, getRevenue(acceptItem) + acceptItem.getAmount());
                    plugin.sendMessage(player, PLAYER_PURCHASE.getPath(), plugin.itemToString(sellableItem),
                            plugin.itemToString(acceptItem));
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
        plugin.data.getConfig().set(name + ".sells", plugin.itemToDataString(item));
        String itemName = plugin.itemToString(item, false);
        initSellableItem();
        plugin.sendMessage(player, SIGN_STOCK_SET.getPath(), Integer.toString(item.getAmount()), itemName);
    }

    public void addAcceptableItem(Player player, ItemStack item) {
        List<String> items = getAcceptableItemsString();
        item.setAmount(1);
        items.add(plugin.itemToDataString(item));
        plugin.data.getConfig().set(name + ".accepts", items);
        initAcceptableItems();
        plugin.sendMessage(player, SIGN_PAYMENT_ADDED.getPath(), plugin.itemToString(item));
    }

    public void removeAcceptableItem(Player player, ItemStack item) {
        item = getAcceptableItems().get(indexOf(item));
        getAcceptableItems().remove(item);
        List<String> items = getAcceptableItemsString();
        int index = indexOfAcceptableItemsString(plugin.itemToDataString(item));
        items.remove(index);
        plugin.data.getConfig().set(name + ".accepts", items);
        plugin.sendMessage(player, SIGN_PAYMENT_REMOVED.getPath(), plugin.itemToString(item));
    }

    public int increaseAcceptableItemAmount(ItemStack item) {
        int index = indexOf(item);
        List<String> items = getAcceptableItemsString();
        int stringIndex = indexOfAcceptableItemsString(plugin.itemToDataString(item));
        item.setAmount(getAcceptableItems().get(index).getAmount() + 1);
        acceptableItems.set(index, item);
        items.set(stringIndex, plugin.itemToDataString(item));
        plugin.data.getConfig().set(name + ".accepts", items);
        return item.getAmount();
    }

    public int decreaseAcceptableItemAmount(ItemStack item) {
        int index = indexOf(item);
        List<String> items = getAcceptableItemsString();
        int stringIndex = indexOfAcceptableItemsString(plugin.itemToDataString(item));
        item.setAmount(getAcceptableItems().get(index).getAmount() - 1);
        acceptableItems.set(index, item);
        items.set(stringIndex, plugin.itemToDataString(item));
        plugin.data.getConfig().set(name + ".accepts", items);
        return item.getAmount();
    }

    public int increaseSellableItemAmount() {
        sellableItem.setAmount(sellableItem.getAmount() + 1);
        plugin.data.getConfig().set(name + ".sells", plugin.itemToDataString(sellableItem));
        return sellableItem.getAmount();
    }

    public int decreaseSellableItemAmount() {
        sellableItem.setAmount(sellableItem.getAmount() - 1);
        plugin.data.getConfig().set(name + ".sells", plugin.itemToDataString(sellableItem));
        return sellableItem.getAmount();
    }

    public ItemStack getSellableItem() {
        return sellableItem;
    }

    public String getSellableItemString() {
        return plugin.data.getConfig().getString(name + ".sells");
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
        List items = plugin.data.getConfig().getList(name + ".accepts");
        if (items == null) items = new ArrayList<Object>();
        List<String> string = new ArrayList<String>();
        for (Object o : items) {
            string.add(o.toString());
        }
        return string;
    }

    public Integer indexOfAcceptableItemsString(String search) {
        List<String> items = getAcceptableItemsString();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).split("\\s")[1].equals(search.split("\\s")[1])) {
                return i;
            }
        }
        return -1;
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
        return plugin.data.getConfig().getInt(name + ".stock", 0);
    }

    public void setStock(int stock) {
        plugin.data.getConfig().set(name + ".stock", stock);
    }

    public Integer getRevenue(ItemStack item) {
        Integer amount = plugin.data.getConfig().getInt(name + ".revenue." + plugin.itemToDataString(item, false), 0);
        return amount;
    }

    public Integer getRevenueStackCount() {
        int stacks = 0;
        for (ItemStack item : this.getAcceptableItems()) {
            int revenue = getRevenue(item);
            int maxStackSize = item.getMaxStackSize();
            if (maxStackSize == -1) maxStackSize = 64;
            stacks += revenue / maxStackSize + (revenue % maxStackSize > 0 ? 1 : 0);
        }
        return stacks;
    }

    public void setRevenue(ItemStack item, int revenue) {
        plugin.data.getConfig().set(name + ".revenue." + plugin.itemToDataString(item, false), revenue);
    }
}
