package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.*;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.dungeon.Selection;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /xdungeon dungeon extensions.
 */
public class DungeonCommands extends XDungeonCommand{
    public DungeonCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"create"}, desc = "Creates a dungeon.", usage = "/xdungeon dungeon create <dungeon name>", min = 1)
    @CommandPermissions({"xd.dungeon.create"})
    public void create(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        doesDungeonExist(name);
        manager.addDungeon(name, player.getWorld().getName());
        player.sendMessage(ChatColor.GREEN + "You have created dungeon: " + ChatColor.RED + name);
    }

    @Command(aliases = {"remove"}, desc = "Removes a dungeon.", usage = "/xdungeon dungeon remove <dungeon name>", min = 1)
    @CommandPermissions({"xd.dungeon.remove"})
    public void remove(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        Dungeon dungeon = getDungeon(name);
        manager.removeDungeon(name);
        player.sendMessage(ChatColor.GREEN + "You have removed dungeon: " + ChatColor.RED + name);
    }

    @Command(aliases = {"setboundaries"}, desc = "Sets the boundaries of the dungeon to your current selection.", usage = "/xdungeon dungeon setboundaries <dungeon name>", min = 1)
    @CommandPermissions({"xd.dungeon.modify"})
    public void setBoundaries(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        Dungeon dungeon = getDungeon(name);
        Selection selection = getSelection(player);
        dungeon.setBoundaries(selection.getPoint1(), selection.getPoint2());
        player.sendMessage(ChatColor.GREEN + "Boundaries set for " + ChatColor.RED + name);
    }

    @Command(aliases = {"maxplayers"}, desc = "Sets the maximum players for the dungeon you are in.", usage = "/xdungeon dungeon maxplayers <max players>", min = 1)
    @CommandPermissions({"xd.dungeon.modify"})
    public void maxPlayers(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        dungeon.setMaxPlayers(args.getInteger(0));
        player.sendMessage(ChatColor.GREEN + "Maximum players set for " + ChatColor.RED + dungeon.getName());
    }

    @Command(aliases = {"firstroom"}, desc = "Sets the first room of the dungeon to the room you are in.", usage = "/xdungeon dungeon firstroom")
    @CommandPermissions({"xd.dungeon.modify"})
    public void firstroom(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        Room room = getRoomIn(player, dungeon);
        dungeon.setFirstRoomName(room.getName());
        player.sendMessage(ChatColor.GREEN + "First room set to " + ChatColor.RED + room.getName());
    }

    @Command(aliases = {"start"}, desc = "Starts the specified dungeon.", usage = "/xdungeon dungeon start <dungeon name>", min = 1)
    @CommandPermissions({"xd.dungeon.execute"})
    public void start(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Party party = partyManager.getParty(player);
        if (party == null) {
            party = partyManager.addParty(player);
        }
        Dungeon dungeon = manager.getDungeon(args.getString(0));
        if (dungeon != null) {
            if (dungeon.getParty() != null) {
                player.sendMessage(ChatColor.RED + dungeon.getName() + ChatColor.GREEN + " is already in use.");
            }
            party.setDungeon(dungeon);
            dungeon.executeDungeon(player, true);
        } else {
            player.sendMessage(ChatColor.RED + "That isn't a valid dungeon, sorry!");
        }
    }

    @Command(aliases = {"end"}, desc = "Ends the current dungeon.", usage = "/xdungeon dungeon end")
    @CommandPermissions({"xd.dungeon.execute"})
    public void end(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        getDungeonIn(player).endDungeon(player, false);
        player.sendMessage(ChatColor.GREEN + "Dungeon ended.");
    }

    @Command(aliases = {"roomlist"}, desc = "Lists the rooms of the dungeon you are in.", usage = "/xdungeon dungeon roomlist")
    @CommandPermissions({"xd.dungeon.modify"})
    public void roomList(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        player.sendMessage(ChatColor.RED + "Rooms:");
        Dungeon dungeon = getDungeonIn(player);
        for (Room room : dungeon.getRooms().values()) {
            String message = ChatColor.GREEN + room.getName() + ", #Spawners: " + ChatColor.RED + room.getSpawners().size();
            if (room == dungeon.getFirstRoom()){
                message = message.concat(ChatColor.GREEN + ", First Room");
            }
            if (!room.getNextRoomName().equals("")) {
                message = message.concat(ChatColor.GREEN + ", Next Room: " + ChatColor.RED + room.getNextRoomName());
            }
            player.sendMessage(message);
        }
    }

    @Command(aliases = {"entrymessage"}, desc = "Sets the entry message for the dungeon you are in.", usage = "/xdungeon dungeon entrymessage <message>", min = 1)
    @CommandPermissions({"xd.dungeon.modify"})
    public void setEntryMessage(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String message = args.getJoinedStrings(0);
        getDungeonIn(player).setEntryMessage(message);
        player.sendMessage(ChatColor.DARK_RED + message);
    }

    @Command(aliases = {"endmessage"}, desc = "Sets the entry message for the dungeon you are in.", usage = "/xdungeon dungeon endmessage <message>", min = 1)
    @CommandPermissions({"xd.dungeon.modify"})
    public void setEndMessage(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String message = args.getJoinedStrings(0);
        getDungeonIn(player).setEndMessage(message);
        player.sendMessage(ChatColor.DARK_GREEN + message);
    }
}