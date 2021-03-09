package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyPlayerJoinEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyPlayerJoinListener implements Listener {

    @EventHandler
    public void onPartyPlayerJoin(PartyPlayerJoinEvent event) {
        ProxyParty.getInstance().getMongo().updateParty(event.getParty());
    }
}