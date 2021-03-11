package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyDisbandEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyDisbandListener implements Listener {

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent ev) {
        ProxyParty.getInstance().getMongo().disbandParty(ev.getParty());
    }
}