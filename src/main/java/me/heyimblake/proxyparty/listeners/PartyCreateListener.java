package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyCreateEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyCreateListener implements Listener {

    @EventHandler
    public void onPartyCreate(PartyCreateEvent ev) {
        ProxyParty.getInstance().getMongo().createParty(ev.getParty());
    }
}