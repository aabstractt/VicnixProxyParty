package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyPlayerQuitEvent;
import me.heyimblake.proxyparty.partyutils.Party;
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

        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
        party.sendPartyMessage(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &ase ha salido de la party.", quitter.getName())));
        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));

        ProxyParty.getInstance().getMongo().updateParty(event.getParty());
    }
}