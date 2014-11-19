package me.xorgon.xdungeon.dungeon;

import me.xorgon.xdungeon.XDLootManager;
import me.xorgon.xdungeon.XDManager;
import me.xorgon.xdungeon.XDPartyManager;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.events.RoomFinishEvent;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * XDungeon room class.
 */
public class Room {
    private XDungeonPlugin plugin = XDungeonPlugin.getInstance();
    private XDManager manager = plugin.getManager();
    private XDLootManager lootManager = plugin.getLootManager();

    private final Dungeon dungeon;
    private final String name;
    private List<Spawner> spawners = new ArrayList();
    private Vector min = null;
    private Vector max = null;
    private Door door = null;
    private Location start = new Location(null, 0, 0, 0, 0, 0);
    private String nextRoomName = "";
    private Map<String, Loot> loot = new HashMap<>();

    private static final int FUZZY_MATCH_RANGE = 3;
    private Room nextRoom;
    private World world = null;
    private int mobsRemaining;

    private Random random = new Random();

    public Room(String name, Dungeon dungeon) {
        this.dungeon = dungeon;
        this.name = name.toLowerCase();
        this.world = dungeon.getWorld();
        door = new Door(world);
    }

    public String getName() {
        return name;
    }

    public List<Spawner> getSpawners() {
        return new ArrayList<>(spawners);
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public void setBoundaries(Vector point1, Vector point2) {
        min = Vector.getMinimum(point1, point2);
        max = Vector.getMaximum(point1, point2);
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        start.setWorld(world);
        this.start = start;
    }

    public String getNextRoomName() {
        return nextRoomName;
    }

    public void setNextRoomName(String name) {
        this.nextRoomName = name;
        nextRoom = dungeon.getRoom(name);
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Room getNextRoom() {
        if (nextRoom != null) {
            return nextRoom;
        } else {
            return null;

        }
    }

    public void setNextRoom(Room nextRoom) {
        this.nextRoom = nextRoom;
        nextRoomName = nextRoom.getName();
    }

    public void addLoot(String name, Double dropChance) {
        loot.put(name, new Loot(name, lootManager.getLootPiece(name), dropChance));
    }

    public void removeLoot(String name) {
        loot.remove(name);
    }

    public Map<String, Loot> getLoot() {
        return loot;
    }

    public void setLoot(Map<String, Loot> loot) {
        this.loot = loot;
    }

    /**
     * Gets a {@link Spawner} by {@link Vector}. There's a {@link #FUZZY_MATCH_RANGE} which specifies the range it looks at.
     *
     * @param vector vector to check
     * @return the {@link Spawner} if any were found, otherwise null
     */
    public Spawner getSpawner(Vector vector) {
        Spawner found = null;
        for (Spawner spawner : this.spawners) {
            if (spawner.getLocation().distance(vector) <= FUZZY_MATCH_RANGE) {
                found = spawner;
                break; // We found the closest spawner next to the given vector.
            }
        }
        return found;
    }

    public Spawner addSpawner(Spawner spawner) {
        this.spawners.add(spawner);
        return spawner;
    }

    public boolean removeSpawner(Spawner spawner) {
        return this.spawners.remove(spawner);
    }

    public int getMobsRemaining() {
        return mobsRemaining;
    }

    public void setMobsRemaining(int mobsRemaining) {
        this.mobsRemaining = mobsRemaining;
    }

    public void setMaxMobsRemaining() {
        mobsRemaining = 0;
        for (Spawner spawner : spawners) {
            mobsRemaining = mobsRemaining + spawner.getQuantity();
        }
    }

    public void executeRoom() {
        setMaxMobsRemaining();
        if (mobsRemaining == 0){
            finishRoom();
        }
        for (Spawner spawner : spawners) {
            spawner.executeSpawner();
        }
    }

    public void finishRoom() {
        Party party = dungeon.getParty();
        if (start != new Location(null, 0, 0, 0, 0, 0)) {
            party.setSpawn(start);
        }
        Map<String, Player> players = party.getMembers();
        Player leader = party.getLeader();
        players.put(leader.getName(), leader);
        for (Player player : players.values()) {
            for (Loot lootPiece : loot.values()) {
                if (random.nextDouble() * 100 <= lootPiece.getDropChance()) {
                    player.getInventory().addItem(lootPiece.getItem());
                }
            }
        }
        players.remove(leader.getName());
        door.openDoor();
        if (nextRoom != null) {
            nextRoom.executeRoom();
        } else {
            dungeon.endDungeon(leader, true);
        }
        RoomFinishEvent event = new RoomFinishEvent("RoomFinishEvent", this.dungeon, this, this.dungeon.getParty());
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public Boolean checkCompleteness() {
        Vector defaultVector = new Vector(0, 0, 0);
        Location defaultLocation = new Location(null, 0, 0, 0, 0, 0);
        if (min == defaultVector || max == defaultVector) {
            return false;
        }
        if (start == defaultLocation) {
            return false;
        }
        return true;
    }
}
