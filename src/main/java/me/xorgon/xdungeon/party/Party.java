package me.xorgon.xdungeon.party;

import me.xorgon.xdungeon.dungeon.Dungeon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * XDungeon party class.
 */
public class Party {
    private Player leader;
    private Map<String, Player> members = new HashMap<>();
    private Map<String, Player> invites = new HashMap<>();
    private Dungeon dungeon = null;
    private Location spawn = null;
    private Map<Player,Location> originalLocs = new HashMap<>();

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public Map<String, Player> getMembers() {
        return members;
    }

    public void addMember(Player player){
        members.put(player.getName(),player);
    }

    public void removeMember(Player player){
        members.remove(player.getName());
    }

    public Map<String, Player> getInvites() {
        return invites;
    }

    public void addInvite(Player player){
        invites.put(player.getName(),player);
    }

    public void removeInvite(Player player){
        invites.remove(player.getName());
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void setOriginalLoc(Player player){
        originalLocs.put(player, player.getLocation());
    }

    public Location getOriginalLoc(Player player){
        return originalLocs.get(player);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}
