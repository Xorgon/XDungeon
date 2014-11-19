package me.xorgon.xdungeon.events;

import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Event called when a dungeon is finished.
 */
public final class DungeonFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String message;
    private Dungeon dungeon;
    private Party party;
    private Map<String, Player> players;
    private Boolean success;

    public DungeonFinishEvent(String message, Dungeon dungeon, Party party, Boolean success){
        this.message = message;
        this.dungeon = dungeon;
        this.party = party;
        players = party.getMembers();
        Player leader = party.getLeader();
        players.put(leader.getName(), leader);
        this.success = success;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Party getParty() {
        return party;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
