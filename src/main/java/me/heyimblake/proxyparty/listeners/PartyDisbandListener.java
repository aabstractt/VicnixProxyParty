package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyDisbandEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyDisbandListener implements Listener {

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        Party party = event.getParty();

        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
        party.sendPartyMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &aha borrado la party!", party.getLeader().getName()))));
        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));

        ProxyParty.getInstance().getMongo().disbandParty(event.getParty());
    }
}