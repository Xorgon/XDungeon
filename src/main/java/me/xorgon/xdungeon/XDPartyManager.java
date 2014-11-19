package me.xorgon.xdungeon;

import me.xorgon.xdungeon.party.Party;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * XDungeonPlugin party manager class.
 */
public class XDPartyManager {

    private Map<Player, Party> parties = new HashMap<>();
    private Map<Player, Player> invites = new HashMap<>();

    public Map<Player, Party> getParties() {
        return parties;
    }

    public Party getParty(Player player) {
        if (parties.containsKey(player)) {
            return parties.get(player);
        }else {
            return null;
        }
    }

    public Party getPartyIn(Player player) {
        for (Party party : parties.values()) {
            for (Player player1 : party.getMembers().values()) {
                if (player == player1) {
                    return party;
                }
            }
        }
        return null;
    }

    public Party addParty(Player leader) {
        Party party = new Party();
        party.setLeader(leader);
        parties.put(leader, party);
        return party;
    }

    public Player getPlayerInvite(Player player) {
        return invites.get(player);
    }

    public void setPlayerInvite(Player invited, Player inviter) {
        invites.put(invited, inviter);
    }
}
