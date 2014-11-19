package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import me.xorgon.xdungeon.XDungeonPlugin;
import org.bukkit.command.CommandSender;

/**
 * /xdungeon commands.
 */

public class ParentCommand extends XDungeonCommand {

    public ParentCommand(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"xdungeon", "xd"},
            desc = "XDungeon command.",
            usage = "/xdungeon <extension>"
    )
    @NestedCommand(Commands.class)
    public void xdungeon(CommandContext args, CommandSender sender){

    }
}
