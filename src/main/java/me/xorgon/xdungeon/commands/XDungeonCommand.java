package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.CommandException;
import me.xorgon.xdungeon.XDLootManager;
import me.xorgon.xdungeon.XDManager;
import me.xorgon.xdungeon.XDPartyManager;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.dungeon.Selection;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


/**
 * Root of commands.
 */
public class XDungeonCommand {
    protected final XDungeonPlugin plugin;
    protected final XDManager manager;
    protected final XDPartyManager partyManager;
    protected final XDLootManager lootManager;

    public XDungeonCommand(XDungeonPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
        this.partyManager = plugin.getPartyManager();
        this.lootManager = plugin.getLootManager();
    }

    public Dungeon getDungeon(String name) throws CommandException {
        Dungeon dungeon = manager.getDungeon(name);
        if (dungeon == null){
            throw new CommandException("Dungeon " + name + " doesn't exist.");
        }
        return dungeon;
    }
    public Dungeon getDungeonIn(Entity entity) throws CommandException {
        Dungeon dungeon = manager.getDungeonIn(entity);
        if (dungeon == null){
            throw new CommandException("You are not in a dungeon.");
        }
        return dungeon;
    }
    public void doesDungeonExist(String name) throws CommandException {
        if (manager.getDungeon(name) != null){
            throw new CommandException("Dungeon " + name + " already exists.");
        }
    }
    public Room getRoom(String name, Dungeon dungeon) throws  CommandException{
        Room room = dungeon.getRoom(name);
        if (room == null){
            throw new CommandException("Room " + name + " doesn't exist.");
        }
        return room;
    }
    public Room getRoomIn(Entity entity, Dungeon dungeon) throws  CommandException {
        Room room = dungeon.getRoomIn(entity);
        if (room == null){
            throw new CommandException("You are not in a room.");
        }
        return room;
    }
    public void doesRoomExist(String name, Dungeon dungeon) throws CommandException {
        if (dungeon.getRoom(name) != null){
            throw new CommandException("Room " + name + " already exists.");
        }
    }
    public Player getPlayer(String name) throws CommandException {
        Player player = Bukkit.getPlayer(name);
        if (player == null){
            throw new CommandException("Player " + name + " is not online.");
        }
        return player;
    }
    public Party getParty(Player player) throws CommandException {
        Party party = partyManager.getParty(player);
        if (party == null){
            throw new CommandException("Party invalid.");
        }
        return party;
    }

    public Selection getSelection(Player player) throws CommandException {
        Selection selection = manager.getSelection(player);
        Vector point1 = selection.getPoint1();
        Vector point2 = selection.getPoint2();
        if (point1 == null || point2 == null){
            throw new CommandException("Selection invalid.");
        }
        return selection;
    }
}
