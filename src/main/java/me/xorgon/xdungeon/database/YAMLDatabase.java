package me.xorgon.xdungeon.database;

import me.xorgon.xdungeon.XDLootManager;
import me.xorgon.xdungeon.XDManager;
import me.xorgon.xdungeon.XDPartyManager;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.*;
import me.xorgon.xdungeon.util.ConfigUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import javax.print.PrintException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.xorgon.xdungeon.util.ConfigUtil.*;

/**
 * XDungeon database class.
 */
public class YAMLDatabase {
    private final File file;
    private XDungeonPlugin plugin = XDungeonPlugin.getInstance();
    private YamlConfiguration config;
    private XDManager manager = plugin.getManager();
    private XDLootManager lootManager = plugin.getLootManager();

    public YAMLDatabase(File file) {
        this.file = file;
    }

    public Map<String, Dungeon> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }
        config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = config.getConfigurationSection("dungeons");
        if (root == null) {
            return new HashMap<>();
        }

        ConfigurationSection loot = config.getConfigurationSection("loot");
        if (loot != null) {
            for (String lootName : loot.getKeys(false)) {
                lootManager.addLoot(lootName, loot.getItemStack(lootName));
            }
        }

        Map<String, Dungeon> dungeons = new HashMap<>();
        for (String dungeonName : root.getKeys(false)) {
            dungeons.put(dungeonName.toLowerCase(), loadDungeon(root.getConfigurationSection(dungeonName)));
        }

        config = null;
        return dungeons;
    }

    public Dungeon loadDungeon(ConfigurationSection dungeonConfig) {
        String world = dungeonConfig.getString("world");
        Validate.notNull(world, "'" + world + "' is not a valid world.");
        Dungeon dungeon = new Dungeon(dungeonConfig.getName(), world);

        dungeon.setWorldName(world);

        String displayName = dungeonConfig.getString("displayName");
        dungeon.setDisplayName(displayName);

        Integer maxPlayers = dungeonConfig.getInt("maxplayers");
        dungeon.setMaxPlayers(maxPlayers);

        String min = dungeonConfig.getString("min");
        String max = dungeonConfig.getString("max");
        if (min != null && max != null) {
            dungeon.setBoundaries(deserializeVector(min), deserializeVector(max));
        }

        ConfigurationSection roomsConfig = dungeonConfig.getConfigurationSection("rooms");
        if (roomsConfig == null) {
            dungeon.setRooms(new HashMap<String, Room>());
        } else {
            for (String roomName : roomsConfig.getKeys(false)) {
                dungeon.addRoom(loadRoom(roomsConfig.getConfigurationSection(roomName), dungeon.getWorld(), dungeon));
            }
            for (Room room : dungeon.getRooms().values()) {
                room.setNextRoomName(roomsConfig.getConfigurationSection(room.getName()).getString("nextRoom"));
            }
            dungeon.setFirstRoomName(dungeonConfig.getString("firstRoom"));
        }
        return dungeon;
    }

    private Room loadRoom(ConfigurationSection roomConfig, World world, Dungeon dungeon) {
        Room room = new Room(roomConfig.getName(), dungeon);
        room.setBoundaries(deserializeVector(roomConfig.get("min")), deserializeVector(roomConfig.get("max")));
        for (Spawner spawners : loadSpawners(roomConfig.getConfigurationSection("spawners"), world)) {
            room.addSpawner(spawners);
        }

        room.setStart(deserializeLocation(roomConfig.get("start"), false));

        ConfigurationSection lootConfig = roomConfig.getConfigurationSection("loot");
        for (String lootName : lootConfig.getKeys(false)) {
            room.addLoot(lootName,lootConfig.getDouble(lootName));
        }

        ConfigurationSection doorsConfig = roomConfig.getConfigurationSection("doors");
        if (doorsConfig != null) {
            Door door = new Door(world);
            if (doorsConfig.get("material") != null) {
                door.setMaterial(deserializeMaterialData(doorsConfig.get("material")));
            }
            door.setBoundaries(deserializeVector(doorsConfig.get("min")), deserializeVector(doorsConfig.get("max")));
            room.setDoor(door);
        }
        return room;
    }

    private List<Spawner> loadSpawners(ConfigurationSection spawnersConfig, World world) {
        List<Spawner> spawners = new ArrayList<>();
        for (String idx : spawnersConfig.getKeys(false)) {
            ConfigurationSection spawnerSection = spawnersConfig.getConfigurationSection(idx);
            Spawner spawner = new Spawner(world, null, 0);
            spawner.setLocation(deserializeVector(spawnerSection.get("spawnPoint")));
            spawner.setMobType(EntityType.valueOf(spawnerSection.getString("mobType")));
            spawner.setQuantity(spawnerSection.getInt("quantity"));
            spawners.add(spawner);
        }
        return spawners;
    }

    public void save(Map<String, Dungeon> dungeons) {
        if (config == null) {
            config = new YamlConfiguration();
        }
        if (config.contains("dungeons")) {
            config.set("dungeons", null);
        } else {
            config.createSection("dungeons");
        }
        ConfigurationSection dungeonsSection = config.getConfigurationSection("dungeons");
        for (Dungeon dungeon : dungeons.values()) {
            ConfigurationSection dungeonSection = dungeonsSection.createSection(dungeon.getName());
            dungeonSection.set("world", dungeon.getWorldName());
            dungeonSection.set("displayName", dungeon.getDisplayName());
            dungeonSection.set("firstRoom", dungeon.getFirstRoomName());
            dungeonSection.set("min", serializeVector(dungeon.getMin()));
            dungeonSection.set("max", serializeVector(dungeon.getMax()));
            dungeonSection.set("maxplayers", dungeon.getMaxPlayers());

            ConfigurationSection roomsSection = dungeonSection.createSection("rooms");
            for (Room room : dungeon.getRooms().values()) {
                ConfigurationSection roomSection = roomsSection.createSection(room.getName());
                roomSection.set("start", serializeLocation(room.getStart(), false));
                roomSection.set("min", serializeVector(room.getMin()));
                roomSection.set("max", serializeVector(room.getMax()));
                roomSection.set("nextRoom", room.getNextRoomName());
                ConfigurationSection lootSection = roomSection.createSection("loot");
                for (Loot loot : room.getLoot().values()) {
                    lootSection.set(loot.getName(),loot.getDropChance());
                }

                if (room.getDoor() != null) {
                    ConfigurationSection doorsSection = roomSection.createSection("doors");
                    if (room.getDoor().getMaterial() != null) {
                        doorsSection.set("material", serializeMaterialData(room.getDoor().getMaterial()));
                    }
                    doorsSection.set("min", serializeVector(room.getDoor().getMin()));
                    doorsSection.set("max", serializeVector(room.getDoor().getMax()));
                }
                ConfigurationSection spawnersSection = roomSection.createSection("spawners");
                for (int s = 1; s <= room.getSpawners().size(); s++) {
                    Spawner spawner = room.getSpawners().get(s - 1);
                    ConfigurationSection spawnerSection = spawnersSection.createSection(String.valueOf(s));
                    spawnerSection.set("spawnPoint", serializeVector(spawner.getLocation()));
                    spawnerSection.set("mobType", spawner.getMobType().toString());
                    spawnerSection.set("quantity", spawner.getQuantity());
                }
            }
        }

        ConfigurationSection lootSection = config.createSection("loot");
        Map<String, ItemStack> loot = lootManager.getLoot();
        if (loot != null) {
            for (String key : loot.keySet()) {
                    lootSection.set(key, loot.get(key));
            }
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }


    }
}

