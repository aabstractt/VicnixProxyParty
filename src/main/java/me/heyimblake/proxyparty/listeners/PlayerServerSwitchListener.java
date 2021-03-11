package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartyRole;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerServerSwitchListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) return;

        if (PartyRole.getRoleOf(player) != PartyRole.LEADER) return;

        party.warpParticipants(player.getServer().getInfo());

        party.getLeader().sendMessage(Constants.TAG, new ComponentBuilder("Los jugadores de tu party se estan moviendo a tu servidor.").color(ChatColor.LIGHT_PURPLE).create()[0]);
    }
}