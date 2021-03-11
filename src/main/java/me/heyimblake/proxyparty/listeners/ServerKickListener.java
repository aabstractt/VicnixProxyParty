package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onServerKickEvent(ServerKickEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) return;

        party.sendPartyMessage(new ComponentBuilder("No se ha podido conectar a todos los miembros de la party al servidor actual").color(ChatColor.RED).create()[0]);
    }
}