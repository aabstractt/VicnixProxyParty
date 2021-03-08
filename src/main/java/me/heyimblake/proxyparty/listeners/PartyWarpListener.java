package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyWarpEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyWarpListener implements Listener {

    @EventHandler
    public void onPartyWarp(PartyWarpEvent event) {
        Party party = event.getParty();

        party.sendMessage("El lider de la party los ha movido a este servidor!", ChatColor.AQUA);
    }
}