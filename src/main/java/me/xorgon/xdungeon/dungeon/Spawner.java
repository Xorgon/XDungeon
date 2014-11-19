package me.xorgon.xdungeon.dungeon;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * XDungeon spawner class.
 */
public class Spawner {
    private EntityType mobType = null;
    private int quantity = 0;
    private Vector location = new Vector(0,0,0);
    private World world;

    public Spawner(World world, EntityType mobType, int quantity){
        this.world = world;
        this.mobType = mobType;
        this.quantity = quantity;
    }

    public EntityType getMobType() {
        return mobType;
    }

    public void setMobType(EntityType mobType) {
        this.mobType = mobType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Vector getLocation() {
        return location;
    }

    public void setLocation(Vector location) {
        this.location = location;
    }

    public void executeSpawner() {
        for (int s = 1; s <= quantity; s++){
            world.spawnEntity(location.toLocation(world), mobType);
        }
    }
}

