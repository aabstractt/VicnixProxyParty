package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyPlayerJoinEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyPlayerJoinListener implements Listener {

    @EventHandler
    public void onPartyPlayerJoin(PartyPlayerJoinEvent event) {
        Party party = event.getParty();

        if (party.getParticipants().size() >= party.getMax()) {
            party.getLeader().sendMessage(Constants.TAG,
                    new ComponentBuilder(
                            String.format("Tu party ha alcanzado el máximo de jugadores (%s).", Constants.MAX_PARTY_SIZE)).color(ChatColor.RED).create()[0]);
        }

        ProxyParty.getInstance().getMongo().updateParty(event.getParty());
    }
}