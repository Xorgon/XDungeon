package me.xorgon.xdungeon;

import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.party.Party;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * XDungeonPlugin listeners class.
 */
public class XDListeners implements Listener {
    private XDungeonPlugin plugin = XDungeonPlugin.getInstance();

    public XDListeners(XDungeonPlugin plugin) {
        this.plugin = plugin;
    }

    FileConfiguration config = plugin.getConfig();
    XDManager manager = plugin.getManager();
    XDPartyManager partyManager = plugin.getPartyManager();

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.PLAYER)) {
            return;
        }
        Dungeon dungeon = manager.getDungeonIn(entity);
        if (dungeon != null) {
            Room room = dungeon.getRoomIn(entity);
            if (room != null) {
                int mobsRemaining = room.getMobsRemaining() - 1;
                room.setMobsRemaining(mobsRemaining);
                if (mobsRemaining == 0) {
                    room.finishRoom();
                }
            }
        }
    }

    @EventHandler
    public void onDungeonExplosion(EntityExplodeEvent event) {
        Dungeon dungeon = manager.getDungeonIn(event.getEntity());
        if (dungeon != null) {
            event.blockList().clear();
            Room room = dungeon.getRoomIn(event.getEntity());
            int mobsRemaining = room.getMobsRemaining() - 1;
            room.setMobsRemaining(mobsRemaining);
            if (mobsRemaining == 0) {
                room.finishRoom();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerRespawnEvent event) {
        Party party = partyManager.getParty(event.getPlayer());
        if (party != null && party.getSpawn() != null) {
            event.setRespawnLocation(party.getSpawn());
        }
    }

    @EventHandler
    public void onDungeonBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (manager.getDungeonIn(player) != null) {
            if (!(player.hasPermission("xd.dungeon.build"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDungeonPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (manager.getDungeonIn(player) != null) {
            if (!(player.hasPermission("xd.dungeon.build"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDungeonEnter(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Dungeon dungeon = manager.getDungeonIn(player);
        if (dungeon != null){
            Room room = dungeon.getRoomIn(player);
            if (room != null){
                if (dungeon.getFirstRoom() == room){
                    Party party = partyManager.getParty(player);
                    if (party == null) {
                        party = partyManager.addParty(player);
                    }
                    if (dungeon.getParty() == null){
                        dungeon.executeDungeon(player, false);
                    }
                }
            }
        }
    }
}