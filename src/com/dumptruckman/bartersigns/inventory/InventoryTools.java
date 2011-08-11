package com.dumptruckman.bartersigns.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dumptruckman
 */
public class InventoryTools {

    public static HashMap<Integer, ItemStack> all(Inventory inventory, Material type, short durability) {
        HashMap<Integer, ? extends ItemStack> allItems = inventory.all(type);
        HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
        for (Map.Entry<Integer, ? extends ItemStack> item : allItems.entrySet()) {
            if (item.getValue().getDurability() == durability) {
                items.put(item.getKey(), item.getValue());
            }
        }
        return items;
    }

    public static boolean remove(Inventory inventory, Material type, short durability, int amount) {
        HashMap<Integer, ? extends ItemStack> allItems = inventory.all(type);
        HashMap<Integer, Integer> removeFrom = new HashMap<Integer, Integer>();
        int foundAmount = 0;
        for (Map.Entry<Integer, ? extends ItemStack> item : allItems.entrySet()) {
            if (item.getValue().getDurability() == durability) {
                if (item.getValue().getAmount() >= amount - foundAmount) {
                    removeFrom.put(item.getKey(), amount - foundAmount);
                    foundAmount = amount;
                } else {
                    foundAmount += item.getValue().getAmount();
                    removeFrom.put(item.getKey(), item.getValue().getAmount());
                }
                if (foundAmount >= amount) {
                    break;
                }
            }
        }
        if (foundAmount == amount) {
            for (Map.Entry<Integer, Integer> toRemove : removeFrom.entrySet()) {
                ItemStack item = inventory.getItem(toRemove.getKey());
                if (item.getAmount() - toRemove.getValue() <= 0) {
                    inventory.clear(toRemove.getKey());
                } else {
                    item.setAmount(item.getAmount() - toRemove.getValue());
                    inventory.setItem(toRemove.getKey(), item);
                }
            }
            return true;
        }
        return false;
    }

    public static List<ItemStack> getSeparatedItems(ItemStack item) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        int numStacks = item.getAmount() / item.getType().getMaxStackSize();
        int remainingAmount = item.getAmount() % item.getType().getMaxStackSize();
        for (int i = 0; i < numStacks; i++) {
            items.add(new ItemStack(item.getType(), item.getType().getMaxStackSize(), item.getDurability()));
        }
        if (remainingAmount > 0) {
            items.add(new ItemStack(item.getType(), remainingAmount, item.getDurability()));
        }
        return items;
    }
}
