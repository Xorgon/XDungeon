package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.dungeon.Door;
import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.dungeon.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 * /xdungeon room door extensions
 */
public class RoomDoorCommands extends XDungeonCommand{

    public RoomDoorCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"setboundaries"}, desc = "Sets the boundaries of the door to your current selection.", usage = "/xdungeon room door setboundaries")
    @CommandPermissions({"xd.dungeon.modify"})
    public void setBoundaries(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Dungeon dungeon = getDungeonIn(player);
        Room room = getRoomIn(player, dungeon);
        Door door = room.getDoor();
        String name = room.getName();
        Selection selection = getSelection(player);
        door.setBoundaries(selection.getPoint1(), selection.getPoint2());
        player.sendMessage(ChatColor.GREEN + "Door boundaries set for " + ChatColor.RED + name);
    }

    @Command(aliases = {"material"}, desc = "Sets the material of this room's door (e.g. wool:4).", usage = "/xdungeon room door material <material>", min = 1)
    @CommandPermissions({"xd.room.modify.door"})
    public void material(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        String[] split = args.getString(0).split(":");
        MaterialData material = new MaterialData(Material.getMaterial(split[0].toUpperCase()), (split.length > 1 ? Byte.parseByte(args.getString(0)) : 0));
        getDungeonIn(player).getRoomIn(player).getDoor().setMaterial(material);
        player.sendMessage(ChatColor.GREEN + "Door material set to " + ChatColor.RED + args.getString(0));
    }

}
