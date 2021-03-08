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

/**
 * Copyright (C) 2017 heyimblake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author heyimblake
 * @since 10/21/2016
 */
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
