package me.xorgon.xdungeon;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * XDungeon loot manager class.
 */
public class XDLootManager {
    private Map<String,ItemStack> loot = new HashMap<>();

    public void addLoot(String name, ItemStack itemStack){
        loot.put(name,itemStack);
    }

    public void removeLoot(String name){
        loot.remove(name);
    }

    public ItemStack getLootPiece(String name){
        return loot.get(name);
    }

    public Map<String,ItemStack> getLoot(){
        return loot;
    }
}
