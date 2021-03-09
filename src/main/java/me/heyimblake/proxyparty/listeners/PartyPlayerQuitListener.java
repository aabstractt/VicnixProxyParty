package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyPlayerQuitEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyPlayerQuitListener implements Listener {

    @EventHandler
    public void onPartyPlayerQuit(PartyPlayerQuitEvent event) {
        Party party = event.getParty();

        ProxiedPlayer quitter = event.getWhoQuit();

        party.sendPartyMessage(new TextComponent(Constants.LINE));
        party.sendPartyMessage(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &ese ha salido de la party.", quitter.getName())));
        party.sendPartyMessage(new TextComponent(Constants.LINE));

        ProxyParty.getInstance().getMongo().updateParty(event.getParty());
    }
}