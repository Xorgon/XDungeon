package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Loot;
import me.xorgon.xdungeon.dungeon.Room;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /xdungeon loot extensions.
 */
public class LootCommands extends XDungeonCommand{

    public LootCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"add"}, desc = "Adds the item in your hand to the loot list.", usage = "/xdungeon loot add <loot name>", min = 1)
    @CommandPermissions({"xd.loot.modify"})
    public void add(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        lootManager.addLoot(name, player.getItemInHand());
        player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.RED + name + ChatColor.GREEN + " to the loot list." );
    }

    @Command(aliases = {"remove"}, desc = "Removes a piece of loot from your current room.", usage = "/xdungeon loot remove <loot name>", min = 1)
    @CommandPermissions({"xd.loot.modify"})
    public void remove(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        lootManager.removeLoot(name);
        player.sendMessage(ChatColor.GREEN + "You have removed " + ChatColor.RED + name + ChatColor.GREEN + " from the loot list." );
    }

    @Command(aliases = {"list"}, desc = "Lists all the loot for this room.", usage = "/xdungeon loot list")
    @CommandPermissions({"xd.loot.modify"})
    public void list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String message = org.bukkit.ChatColor.RED + "Loot: ";
        for (String lootPiece : lootManager.getLoot().keySet()) {
            message = message.concat(ChatColor.GREEN + lootPiece + ", ");
        }
        player.sendMessage(ChatColor.GREEN + message);
    }

    @Command(aliases = {"give"}, desc = "Gives you the selected loot piece.", usage = "/xdungeon loot give <Loot piece name>", min = 1)
    @CommandPermissions({"xd.loot.give"})
    public void give(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String name = args.getString(0);
        player.getInventory().addItem(lootManager.getLootPiece(name));
        player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.RED + name);
    }
}
