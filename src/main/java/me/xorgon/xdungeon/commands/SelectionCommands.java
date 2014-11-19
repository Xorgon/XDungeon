package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import me.xorgon.xdungeon.XDungeonPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /xdungeon select commands.
 */
public class SelectionCommands extends XDungeonCommand {

    public SelectionCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"point1"}, desc = "Sets the first limit of a selection to the block you are facing.", usage = "/xdungeon select point1")
    public void point1(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        manager.getSelection(player).setPoint1(player.getTargetBlock(null, 32).getLocation().toVector());
        player.sendMessage(ChatColor.GREEN + "Point1 set.");
    }

    @Command(aliases = {"point2"}, desc = "Sets the second limit of a selection to the block you are facing.", usage = "/xdungeon select point2")
    public void point2(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        manager.getSelection(player).setPoint2(player.getTargetBlock(null, 32).getLocation().toVector());
        player.sendMessage(ChatColor.GREEN + "Point2 set.");
    }
}
