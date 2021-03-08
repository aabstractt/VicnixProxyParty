package me.heyimblake.proxyparty.events;

import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PartyPlayerJoinEvent extends Event {

    private final Party party;
    private final ProxiedPlayer player;

    public PartyPlayerJoinEvent(Party party, ProxiedPlayer player) {
        this.party = party;
        this.player = player;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getWhoJoined() {
        return this.player;
    }
}
