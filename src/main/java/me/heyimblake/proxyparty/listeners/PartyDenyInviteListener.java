package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyDenyInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyDenyInviteListener implements Listener {

    @EventHandler
    public void onPartyDenyInvite(PartyDenyInviteEvent event) {
        Party party = event.getParty();

        ProxiedPlayer denier = event.getDenier();

        party.sendPartyMessage(ChatColor.AQUA + denier.getName() + ChatColor.YELLOW + " ha rechazado la invitacion a tu party!");
    }
}