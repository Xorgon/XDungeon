package me.xorgon.xdungeon.events;

import me.xorgon.xdungeon.dungeon.Dungeon;
import me.xorgon.xdungeon.dungeon.Room;
import me.xorgon.xdungeon.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Event called when a room is finished in a dungeon.
 */
public final class RoomFinishEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private String message;
    private Dungeon dungeon;
    private Room room;
    private Party party;
    private Map<String, Player> players;

    public RoomFinishEvent(String message, Dungeon dungeon, Room room, Party party) {
        this.message = message;
        this.dungeon = dungeon;
        this.room = room;
        this.party = party;
        players = party.getMembers();
        Player leader = party.getLeader();
        players.put(leader.getName(), leader);
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

    public Room getRoom() {
        return room;
    }

    public Party getParty() {
        return party;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
