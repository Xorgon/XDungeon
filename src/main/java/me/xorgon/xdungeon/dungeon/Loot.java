package me.xorgon.xdungeon.dungeon;

import org.bukkit.inventory.ItemStack;

/**
 * Loot class.
 */
public class Loot {

    private String name;
    private ItemStack item;
    private Double dropChance;

    public Loot(String name, ItemStack item, Double dropChance) {
        this.name = name;
        this.item = item;
        this.dropChance = dropChance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public Double getDropChance() {
        return dropChance;
    }

    public void setDropChance(Double dropChance) {
        this.dropChance = dropChance;
    }

}
