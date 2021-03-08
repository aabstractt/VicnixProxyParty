package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyRetractInviteEvent;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyRetractInviteListener implements Listener {

    @EventHandler
    public void onPartyRetractInvite(PartyRetractInviteEvent event) {
        ProxiedPlayer retracted = event.getRetracted();
        ProxiedPlayer retractor = event.getRetractor();

        retracted.sendMessage(Constants.TAG, new ComponentBuilder(String.format("%s ha removido tu invitacion de la party.",
                retractor.getName())).color(ChatColor.GREEN).create()[0]);
    }
}