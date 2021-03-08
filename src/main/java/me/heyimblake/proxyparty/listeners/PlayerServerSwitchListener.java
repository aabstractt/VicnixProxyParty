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

        if (!PartyManager.getInstance().hasParty(player)) return;

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (PartyRole.getRoleOf(player) == PartyRole.LEADER) {
            party.warpParticipants(player.getServer().getInfo());

            party.getLeader().sendMessage(Constants.TAG, new ComponentBuilder(
                    "Los jugadores de tu party se estan moviendo a tu servidor.").color(ChatColor.LIGHT_PURPLE).create()[0]);
            return;
        }
//        if (!player.getServer().getInfo().getName().equalsIgnoreCase(leader.getServer().getInfo().getName())) {
//
//
//            player.sendMessage(Constants.TAG, new ComponentBuilder("Solo los lideres puede moverse a los servidores.!").
//                    color(ChatColor.RED).bold(true).create()[0]);
    }
    /**
     *
     ProxiedPlayer player = event.getPlayer();
     if (!PartyManager.getInstance().hasParty(player))
     return;
     Party party = PartyManager.getInstance().getPartyOf(player);
     ProxiedPlayer leader = party.getLeader();
     if (PartyRole.getRoleOf(player) == PartyRole.LEADER) {
     party.warpParticipants(player.getServer().getInfo());
     party.getLeader().sendMessage(Constants.TAG, new ComponentBuilder(
     "Los jugadores de tu party se estan moviendo a tu servidor.").color(ChatColor.AQUA).create()[0]);
     return;
     }
     if (!player.getServer().getInfo().getName().equalsIgnoreCase(leader.getServer().getInfo().getName())) {


     player.sendMessage(Constants.TAG, new ComponentBuilder("Solo los lideres puede moverse a los servidores.!").
     color(ChatColor.RED).bold(true).create()[0]);

     }
     */
}
