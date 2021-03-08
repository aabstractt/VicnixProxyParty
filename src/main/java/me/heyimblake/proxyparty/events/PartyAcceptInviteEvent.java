package me.heyimblake.proxyparty.events;

import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PartyAcceptInviteEvent extends Event {

    private final Party party;
    private final ProxiedPlayer accepter;

    public PartyAcceptInviteEvent(Party party, ProxiedPlayer accepter) {
        this.party = party;
        this.accepter = accepter;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getAccepter() {
        return this.accepter;
    }
}
