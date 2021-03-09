package me.heyimblake.proxyparty.events;

import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PartyPromoteEvent extends Event {

    private final Party party;
    private final ProxiedPlayer player;
    private final ProxiedPlayer promoter;

    public PartyPromoteEvent(Party party, ProxiedPlayer player, ProxiedPlayer promoter) {
        this.party = party;
        this.player = player;
        this.promoter = promoter;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getPromoted() {
        return this.player;
    }

    public ProxiedPlayer getPromoter() {
        return this.promoter;
    }
}
