package me.xorgon.xdungeon.dungeon;

import me.xorgon.xdungeon.XDPartyManager;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.events.DungeonFinishEvent;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * XDungeon Dungeon class.
 */
public class Dungeon {
    private XDungeonPlugin plugin = XDungeonPlugin.getInstance();
    private XDPartyManager partyManager = plugin.getPartyManager();

    private final String name;
    private String displayName = "";
    private String worldName = "";
    private Vector min = new Vector(0, 0, 0);
    private Vector max = new Vector(0, 0, 0);
    private Map<String, Room> rooms = new HashMap<>();
    private String firstRoomName = "default";
    private Integer maxPlayers = 0;

    private Party party;
    private Room firstRoom = null;
    private World world = null;
    private String entryMessage = "Welcome to my underground layer!";
    private String endMessage = "You have bested me!";
    private Boolean teleportedTo;

    public Dungeon(String name, String worldName) {
        this.name = name.toLowerCase();
        setWorldName(worldName);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String name) {
        worldName = name;
        world = Bukkit.getWorld(worldName);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        this.worldName = world.getName();
    }

    public void setBoundaries(Vector point1, Vector point2) {
        min = Vector.getMinimum(point1, point2);
        max = Vector.getMaximum(point1, point2);
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public Room getRoom(String roomName) {
        Room room = rooms.get(roomName.toLowerCase());
        if (room != null) {
            return room;
        } else {
            return null;
        }
    }

    public void addRoom(Room room) {
        rooms.put(room.getName(), room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room.getName());
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }

    public String getFirstRoomName() {
        return firstRoomName;
    }

    public void setFirstRoomName(String name) {
        firstRoomName = name;
        firstRoom = getRoom(firstRoomName);
    }

    public Party getParty() {
        if (party != null) {
            return party;
        } else {
            return null;
        }
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Room getFirstRoom() {
        return firstRoom;
    }

    public void setFirstRoom(Room firstRoom) {
        this.firstRoom = firstRoom;
        firstRoomName = firstRoom.getName();
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Room getRoomIn(Entity entity) {
        for (Room room : rooms.values()) {
            Vector min = room.getMin();
            Vector max = room.getMax();
            World world = this.getWorld();
            if (entity.getWorld().equals(world) && entity.getLocation().toVector().isInAABB(min, max)) {
                return room;
            }
        }
        return null;
    }

    public void closeAllDoors() {
        for (Room room : rooms.values()) {
            Door door = room.getDoor();
            if (door.checkCompleteness()) {
                door.closeDoor();
            }
        }
    }

    public void executeDungeon(Player leader, Boolean teleport) {
        this.teleport = teleport;
        Party party = partyManager.getParty(leader);
        if (party.getMembers().size() + 1 > maxPlayers && maxPlayers != 0){
            leader.sendMessage(ChatColor.RED + "You have too many players, the maximum players for " + ChatColor.GREEN + this.name + ChatColor.RED +  " is " + ChatColor.GREEN + maxPlayers);
            return;
        }
        if(!checkCompletenes()){
            leader.sendMessage(ChatColor.RED + name + ChatColor.GREEN + " is not complete.");
            return;
        }
        closeAllDoors();
        Location start = firstRoom.getStart();
        leader.sendMessage(ChatColor.DARK_RED + entryMessage);
        party.setOriginalLoc(leader);
        party.setSpawn(start);
        if (teleport) {
            leader.teleport(start);
        }
        this.party = party;
        if (party.getMembers() != null) {
            for (Player player : party.getMembers().values()) {
                party.setOriginalLoc(player);
                if (teleport) {
                    player.teleport(start);
                }
                player.sendMessage(ChatColor.DARK_RED + entryMessage);
            }
        }
        firstRoom.executeRoom();

    }

    public void endDungeon(Player leader, Boolean complete) {
        Party party = partyManager.getParty(leader);
        if(complete) {
          leader.sendMessage(ChatColor.DARK_GREEN + endMessage);
          leader.teleport(party.getOriginalLoc(leader));
            if (party.getMembers() != null) {
                for (Player player : party.getMembers().values()) {
                    player.sendMessage(ChatColor.DARK_GREEN + endMessage);
                    player.teleport(party.getOriginalLoc(player));
                }
            }
            DungeonFinishEvent event = new DungeonFinishEvent(null, this, this.getParty(), true);
            Bukkit.getServer().getPluginManager().callEvent(event);
        } else {
            leader.sendMessage(ChatColor.DARK_RED + "Dungeon ended.");
            leader.teleport(party.getOriginalLoc(leader));
            if (party.getMembers() != null) {
                for (Player player : party.getMembers().values()) {
                    if (!(party.getLeader() == player)) {
                        player.sendMessage(ChatColor.DARK_RED + "Dungeon ended by party leader.");
                        player.teleport(party.getOriginalLoc(player));
                    }
                }
            }
            DungeonFinishEvent event = new DungeonFinishEvent("DungeonFinishEvent", this, this.getParty(), false);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        party.setSpawn(null);
        if (party.getMembers().size() == 0){
            partyManager.getParties().remove(leader);
        }
        this.party = null;
        for (LivingEntity entity : world.getLivingEntities()) {
            if (!(entity instanceof Player)){
                if (getRoomIn(entity) != null){
                    entity.remove();
                }
            }
        }
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }

    public Boolean checkCompletenes(){
        Vector defaultVector = new Vector(0,0,0);
        if (min == defaultVector || max == defaultVector){
            return false;
        }
        if(rooms.size() == 0){
          return false;
        }
        if(worldName.equals("")){
            return false;
        }
        if(firstRoomName.equals("default")){
            return false;
        }
        for (Room room : rooms.values()) {
            if(!room.checkCompleteness()){
                return false;
            }
        }
        return true;
    }
}
