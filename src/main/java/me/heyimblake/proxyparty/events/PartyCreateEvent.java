package me.heyimblake.proxyparty.events;

import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PartyCreateEvent extends Event {

    private final Party party;

    public PartyCreateEvent(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getCreator() {
        return this.getParty().getLeader();
    }
}
