package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyDenyInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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
 * @since 11/03/2016
 */
public class PartyDenyInviteListener implements Listener {
    @EventHandler
    public void onPartyDenyInvite(PartyDenyInviteEvent event) {
        Party party = event.getParty();

        ProxiedPlayer denier = event.getDenier();

        party.sendPartyMessage(ChatColor.AQUA + denier.getName() + ChatColor.YELLOW + " ha rechazado la invitacion a tu party!");
    }
}
