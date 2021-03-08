package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyAcceptInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyAcceptInviteListener implements Listener {

    @EventHandler
    public void onPartyAcceptInvite(PartyAcceptInviteEvent event) {
        Party party = event.getParty();

        ProxiedPlayer accepter = event.getAccepter();

        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
        party.sendPartyMessage(ChatColor.LIGHT_PURPLE + accepter.getName() + ChatColor.GREEN + " se ha unido a la party!");
        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
    }
}