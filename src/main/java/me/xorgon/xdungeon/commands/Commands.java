package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.*;
import com.sk89q.minecraft.util.commands.ChatColor;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /xdungeon command extensions.
 */
public class Commands extends XDungeonCommand {

    private XDungeonPlugin plugin;

    public Commands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"dungeon"}, desc = "Dungeon commands.", usage = "/xdungeon dungeon create/remove/point1/point2/firstroom/start/end")
    @NestedCommand(DungeonCommands.class)
    public void dungeon(CommandContext args, CommandSender sender) {
    }

    @Command(aliases = {"room"}, desc = "Room commands.", usage = "/xdungeon room create/remove/point1/point2/door/loot")
    @NestedCommand(RoomCommands.class)
    public void room(CommandContext args, CommandSender sender) {
    }

    @Command(aliases = {"loot"}, desc = "Loot commands.", usage = "/xdungeon loot add/remove")
    @NestedCommand(LootCommands.class)
    public void loot(CommandContext args, CommandSender sender) {
    }

    @Command(aliases = {"party"}, desc = "Party commands.", usage = "/xdungeon party invite/accept/refuse/list/kick/leave/disband")
    @NestedCommand(PartyCommands.class)
    public void party(CommandContext args, CommandSender sender) {
    }

    @Command(aliases = {"select"}, desc = "Selection commands", usage = "/xdungeon select point1/point2")
    @NestedCommand(SelectionCommands.class)
    @CommandPermissions({"xd.select"})
    public void select(CommandContext args, CommandSender sender) {
    }

    @Command(aliases = {"list"}, desc = "Lists the available dungeons", usage = "/xdungeon list")
    @CommandPermissions({"xd.dungeon.execute"})
    public void list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        player.sendMessage(ChatColor.RED + "Dungeons:");
        for (Dungeon dungeon : manager.getDungeons().values()) {
            String message = org.bukkit.ChatColor.GREEN + dungeon.getName();
            Integer maxPlayers = dungeon.getMaxPlayers();
            if (maxPlayers != 0){
                message = message.concat(" (Max Players: " + org.bukkit.ChatColor.RED + maxPlayers + org.bukkit.ChatColor.GREEN + ")");
            }
            player.sendMessage(message);
        }
    }
}
