package me.xorgon.xdungeon.commands;

import com.sk89q.minecraft.util.commands.*;
import me.xorgon.xdungeon.XDungeonPlugin;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /xdungeon party extensions.
 */
public class PartyCommands extends XDungeonCommand {

    public PartyCommands(XDungeonPlugin plugin) {
        super(plugin);
    }

    @Command(aliases = {"invite"}, desc = "Invites a player to your party.", usage = "/xdungeon party invite <player>", min = 1)
    @CommandPermissions({"xd.party.manage"})
    public void invite(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        if (partyManager.getParty(player) == null) {
            partyManager.addParty(player);
        }
        for (Party party : partyManager.getParties().values()) {
            if (party.getInvites().containsValue(player)) {
                player.sendMessage(org.bukkit.ChatColor.RED + "That player has already been invited to a party.");
            } else if (party.getMembers().containsValue(player)) {
                player.sendMessage(org.bukkit.ChatColor.RED + "That player is already in a party.");
            } else {
                Player invited = getPlayer(args.getString(0));
                party = partyManager.getParty(player);
                party.addInvite(invited);
                invited.sendMessage(org.bukkit.ChatColor.RED + player.getName() + org.bukkit.ChatColor.GREEN + " has invited you to a party. /xd party accept or /xd party refuse");
            }
        }
    }

    @Command(aliases = {"accept"}, desc = "Accepts your current invite.", usage = "/xdungeon party accept")
    @CommandPermissions({"xd.party.participate"})
    public void accept(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        for (Party party : partyManager.getParties().values()) {
            if (party.getInvites().containsValue(player)) {
                if (party.getDungeon() != null){
                    player.sendMessage(ChatColor.RED + "You cannot join a party during a dungeon.");
                    return;
                }
                party.addMember(player);
                party.removeInvite(player);
                Player leader = party.getLeader();
                player.sendMessage(org.bukkit.ChatColor.GREEN + "You have joined " + org.bukkit.ChatColor.RED + leader.getName() + org.bukkit.ChatColor.GREEN + "'s party.");
                leader.sendMessage(org.bukkit.ChatColor.RED + player.getName() + org.bukkit.ChatColor.GREEN + " has accepted your party invitation.");
            }
        }
    }

    @Command(aliases = {"refuse"}, desc = "Refuses your current invite.", usage = "/xdungeon party refuse")
    @CommandPermissions({"xd.party.participate"})
    public void refuse(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        for (Party party : partyManager.getParties().values()) {
            if (party.getInvites().containsValue(player)) {
                party.removeInvite(player);
                Player leader = party.getLeader();
                player.sendMessage(org.bukkit.ChatColor.GREEN + "You have refused " + org.bukkit.ChatColor.RED + leader.getName() + org.bukkit.ChatColor.GREEN + "'s party invite.");
                leader.sendMessage(org.bukkit.ChatColor.RED + player.getName() + org.bukkit.ChatColor.GREEN + " has refused your party invitation.");
            }
        }
    }

    @Command(aliases = {"kick"}, desc = "Kicks the player from the party.", usage = "/xdungeon party kick <player>", min = 1)
    @CommandPermissions({"xd.party.manage"})
    public void kick(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Player kicked = getPlayer(args.getString(0));
        Party party = getParty(player);
        party.removeMember(kicked);
        player.sendMessage(ChatColor.RED + kicked.getName() + ChatColor.GREEN + " has been kicked.");
    }

    @Command(aliases = {"leave"}, desc = "Leaves the party you are in.", usage = "/xdungeon party leave")
    @CommandPermissions({"xd.party.participate"})
    public void leave(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Party party = partyManager.getPartyIn(player);
        party.removeMember(player);
        if (party.getDungeon() != null){
            player.teleport(party.getOriginalLoc(player));
        }
        party.getLeader().sendMessage(ChatColor.RED + player.getName() + ChatColor.GREEN + " has left the party.");
        player.sendMessage(org.bukkit.ChatColor.GREEN + "You have left the party.");
    }

    @Command(aliases = {"list"}, desc = "Lists the players in your party.", usage = "/xdungeon party list")
    @CommandPermissions({"xd.party.participate"})
    public void list(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Party party = partyManager.getPartyIn(player);
        if (party == null) {
            party = getParty(player);
        }
        String members = org.bukkit.ChatColor.RED + "Members: " + org.bukkit.ChatColor.GREEN;
        for (String member : party.getMembers().keySet()) {
            members = members.concat(member + ", ");
        }
        player.sendMessage(members);
        String invites = org.bukkit.ChatColor.RED + "Invites: " + org.bukkit.ChatColor.GREEN;
        for (String invitee : party.getInvites().keySet()) {
            invites = invites.concat(invitee + ", ");
        }
        player.sendMessage(invites);
    }

    @Command(aliases = {"disband"}, desc = "Disbands your party.", usage = "/xdungeon party disband")
    @CommandPermissions({"xd.party.manage"})
    public void disband(CommandContext args, CommandSender sender) throws CommandException {
        Player player = (Player) sender;
        Party party = getParty(player);
        partyManager.getParties().remove(player);
        for (Player player1 : party.getMembers().values()) {
            player1.sendMessage(org.bukkit.ChatColor.RED + player.getName() + org.bukkit.ChatColor.GREEN + "'s party has been disbanded.");
        }
        player.sendMessage(org.bukkit.ChatColor.GREEN + "Your party has been disbanded.");
    }



}


