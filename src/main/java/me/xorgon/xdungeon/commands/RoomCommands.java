package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.*;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.dungeon.Selection;
import me.xorgon.xdungeon.dungeon.Spawner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * /xdungeon room extensions.
 */
public class RoomCommands extends XDungeonCommand{

    public RoomCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"create"}, desc = "Creates a room for the dungeon you are currently in.", usage = "/xdungeon room create <room name>", min = 1)
    @CommandPermissions({"xd.room.create"})
    public void create(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        String name = args.getString(0);
        doesRoomExist(name, dungeon);
        dungeon.addRoom(new Room(name, dungeon));
        player.sendMessage(ChatColor.GREEN + "You have created room: " + ChatColor.RED + name);
    }

    @Command(aliases = {"remove"}, desc = "Removes specified room for the dungeon you are currently in.", usage = "/xdungeon room remove <room name>", min = 1)
    @CommandPermissions({"xd.room.remove"})
    public void remove(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        String name = args.getString(0);
        Room room = getRoom(name, dungeon);
        dungeon.removeRoom(room);
        player.sendMessage(ChatColor.GREEN + "You have removed room: " + ChatColor.RED + name);
    }

    @Command(aliases = {"setboundaries"}, desc = "Sets the boundaries of the room to your current selection.", usage = "/xdungeon room setboundaries <room name>", min = 1)
    @CommandPermissions({"xd.room.modify"})
    public void setBoundaries(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        Dungeon dungeon = getDungeonIn(player);
        Room room = dungeon.getRoom(name);
        Selection selection = getSelection(player);
        room.setBoundaries(selection.getPoint1(), selection.getPoint2());
        player.sendMessage(ChatColor.GREEN + "Boundaries set for " + ChatColor.RED + name);
    }

    @Command(aliases = {"nextroom"}, desc = "Sets the room to be executed after the room you are in.", usage = "/xdungeon room nextroom <room name>", min = 1)
    @CommandPermissions({"xd.room.modify"})
    public void nextRoom(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        Room room = getRoomIn(player, dungeon);
        String name = args.getString(0);
        room.setNextRoom(getRoom(name, dungeon));
        player.sendMessage(ChatColor.GREEN + "Next room set to " + ChatColor.RED + name);
    }

    @Command(aliases = {"loot"}, desc = "XDungeon room loot extensions.", usage = "/xdungeon room loot add/remove")
    @CommandPermissions({"xd.room.modify"})
    @NestedCommand(RoomLootCommands.class)
    public void loot(CommandContext args, CommandSender sender) throws CommandException {}

    @Command(aliases = {"door"}, desc = "XDungeon room door extensions.", usage = "/xdungeon room door point1/point2/material")
    @CommandPermissions({"xd.room.modify.door"})
    @NestedCommand(RoomDoorCommands.class)
    public void door(CommandContext args, CommandSender sender) throws CommandException {}

    @Command(aliases = {"setstart"}, desc = "Sets the start point for the room you are in.", usage = "/xdungeon room setstart")
    @CommandPermissions({"xd.room.modify"})
    public void setStart(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Room room = getDungeonIn(player).getRoomIn(player);
        room.setStart(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Start set for " + ChatColor.RED + room.getName());
    }

    @Command(aliases = {"addspawner"}, desc = "Adds a spawner to the room you are in.", usage = "/xdungeon room addspawner <Mob type> <Quantity>", min = 2)
    @CommandPermissions({"xd.room.modify"})
    public void addSpawner(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Room room = getDungeonIn(player).getRoomIn(player);
        Spawner spawner = room.addSpawner(new Spawner(player.getWorld(), EntityType.valueOf(args.getString(0).toUpperCase()), args.getInteger(1)));
        spawner.setLocation(player.getLocation().toVector());
        player.sendMessage(ChatColor.RED + args.getString(1) + " " + args.getString(0) + ChatColor.GREEN + " spawner created.");
    }
}
