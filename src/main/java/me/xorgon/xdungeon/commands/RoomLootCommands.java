package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.minecraft.util.commands.ChatColor;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Loot;
import me.xorgon.xdungeon.dungeon.Room;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Elijah on 15/10/2014.
 */
public class RoomLootCommands extends XDungeonCommand {

    public RoomLootCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"add"}, desc = "Add a piece of loot to your current room.", usage = "/xdungeon room loot add <loot name> <%Drop Chance>", min = 2)
    @CommandPermissions({"xd.room.modify"})
    public void add(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Room room = getDungeonIn(player).getRoomIn(player);
        String name = args.getString(0);
        room.addLoot(name,args.getDouble(1));
        player.sendMessage(ChatColor.RED + name + org.bukkit.ChatColor.GREEN + " has been added to the loot list.");
    }

    @Command(aliases = {"remove"}, desc = "Removes a piece of loot from your current room.", usage = "/xdungeon room loot remove <loot name>", min = 1)
    @CommandPermissions({"xd.room.modify"})
    public void remove(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Room room = getDungeonIn(player).getRoomIn(player);
        String name = args.getString(0);
        room.removeLoot(name);
        player.sendMessage(ChatColor.RED + name + org.bukkit.ChatColor.GREEN + " has been added to the loot list.");
    }

    @Command(aliases = {"list"}, desc = "Lists all the loot for this room.", usage = "/xdungeon room loot list")
    @CommandPermissions({"xd.room.modify"})
    public void list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Room room = getDungeonIn(player).getRoomIn(player);
        player.sendMessage(org.bukkit.ChatColor.RED + "Loot:");
        for (Loot loot : room.getLoot().values()) {
            player.sendMessage(org.bukkit.ChatColor.GREEN + loot.getName() + ", " + org.bukkit.ChatColor.RED + loot.getDropChance().toString() + "%");
        }
    }
}
