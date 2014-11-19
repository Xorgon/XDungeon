package me.xorgon.xdungeon;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import me.xorgon.xdungeon.commands.ParentCommand;
import me.xorgon.xdungeon.database.YAMLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * XDungeonPlugin main class.
 */
public class XDungeonPlugin extends JavaPlugin {

    private static XDungeonPlugin instance;

    public XDungeonPlugin() {
        instance = this;
    }

    private XDLootManager lootManager;
    private XDManager manager;
    private XDPartyManager partyManager;
    private CommandsManager<CommandSender> commands;
    private CommandsManagerRegistration commandsRegistration;

    @Override
    public void onEnable() {
        lootManager = new XDLootManager();
        partyManager = new XDPartyManager();
        manager = new XDManager(instance);
        Bukkit.getPluginManager().registerEvents(new XDListeners(this), this);
        this.commands = new BukkitCommandsManager();
        this.commands.setInjector(new SimpleInjector(this));
        this.commandsRegistration = new CommandsManagerRegistration(this, this.commands);
        this.commandsRegistration.register(ParentCommand.class);
    }

    public void onDisable() {
        manager.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred. Please notify an administrator.");
            e.printStackTrace();
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public static XDungeonPlugin getInstance() {
        return instance;
    }

    public XDManager getManager() {
        return manager;
    }


    public XDPartyManager getPartyManager() {
        return partyManager;
    }

    public XDLootManager getLootManager() {
        return lootManager;
    }
}
