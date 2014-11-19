package me.xorgon.xdungeon.dungeon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * XDungeon door class.
 */
public class Door {
    private MaterialData material = null;
    private Vector min = new Vector(0,0,0);
    private Vector max = new Vector(0,0,0);

    private World world = null;

    public Door(World world){
        this.world = world;
    }

    public MaterialData getMaterial() {
        return material;
    }

    public void setMaterial(MaterialData material) {
        this.material = material;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public void setBoundaries(Vector point1, Vector point2){
        min = Vector.getMinimum(point1, point2);
        max = Vector.getMaximum(point1, point2);
    }

    public void openDoor(){
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public void closeDoor(){
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    world.getBlockAt(x, y, z).setType(material.getItemType());
                }
            }
        }
    }

    public Boolean checkCompleteness() {
        Vector defaultVector = new Vector(0, 0, 0);
        Location defaultLocation = new Location(null, 0, 0, 0, 0, 0);
        if (min == defaultVector || max == defaultVector) {
            return false;
        }
        if (material == null) {
            return false;
        }
        return true;
    }

}
