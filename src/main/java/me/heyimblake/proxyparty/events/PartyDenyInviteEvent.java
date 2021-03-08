package me.heyimblake.proxyparty.events;

import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PartyDenyInviteEvent extends Event {
    private final Party party;
    private final ProxiedPlayer denier;

    public PartyDenyInviteEvent(Party party, ProxiedPlayer denier) {
        this.party = party;
        this.denier = denier;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getDenier() {
        return this.denier;
    }
}
