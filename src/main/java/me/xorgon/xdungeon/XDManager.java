package me.xorgon.xdungeon;

import me.xorgon.xdungeon.database.YAMLDatabase;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Selection;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * XDungeon Dungeon manager class. Contains all tracking maps.
 */
public class XDManager {
    private final XDungeonPlugin plugin;
    private final YAMLDatabase database;
    private Map<String, Dungeon> dungeons;

    private Map<Player, Selection> selections = new HashMap<>();

    public XDManager(XDungeonPlugin plugin) {
        this.plugin = plugin;
        database = new YAMLDatabase(new File(plugin.getDataFolder(), "dungeons.yml"));
        dungeons = database.load();
    }

    public void save() {
        database.save(dungeons);
    }

    //Gets dungeon based on name.
    public Dungeon getDungeon(String name) {
        return dungeons.get(name);
    }

    //Gets dungeon based on entity location.
    public Dungeon getDungeonIn(Entity entity) {
        for (Dungeon dungeon : dungeons.values()) {
            Vector min = dungeon.getMin();
            Vector max = dungeon.getMax();
            World world = dungeon.getWorld();
            if (entity.getWorld().equals(world) && entity.getLocation().toVector().isInAABB(min, max)) {
                return dungeon;
            }
        }
        return null;
    }

    public void addDungeon(String name, String worldName) {
        dungeons.put(name, new Dungeon(name, worldName));
    }

    public void removeDungeon(String name) {
            dungeons.remove(name);
    }

    public Map<String, Dungeon> getDungeons() {
        return dungeons;
    }

    public Selection getSelection(Player player){
        Selection selection = selections.get(player);
        if (selection == null){
            selection = new Selection(player);
            selections.put(player, selection);
        }
        return selection;
    }

    public Map<Player, Selection> getSelections() {
        return selections;
    }

    public void setSelections(Map<Player, Selection> selections) {
        this.selections = selections;
    }
}
