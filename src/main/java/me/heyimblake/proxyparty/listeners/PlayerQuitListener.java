package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartyRole;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!PartyManager.getInstance().hasParty(player)) return;

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party.getAllParticipants().size() <= 2) {
            party.disband();

            return;
        }

        if (PartyRole.getRoleOf(player) == PartyRole.LEADER) {
            party.setLeader();
        }

        party.removeParticipant(player);

        PartyManager.getInstance().getActiveParties().forEach(party1 -> party1.getInvited().remove(player));
    }
}