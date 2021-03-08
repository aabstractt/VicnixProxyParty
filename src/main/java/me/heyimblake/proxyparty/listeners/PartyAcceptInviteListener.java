package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.events.PartyAcceptInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.utils.ActionLogEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
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
public class PartyAcceptInviteListener implements Listener {
    @EventHandler
    public void onPartyAcceptInvite(PartyAcceptInviteEvent event) {
        Party party = event.getParty();

        ProxiedPlayer accepter = event.getAccepter();

        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));
        party.sendPartyMessage(ChatColor.GOLD + accepter.getName() + ChatColor.YELLOW + " se ha unido a la party!");
        party.sendPartyMessage(new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------"));

//        ProxyParty.getInstance().getRedis().updateMembers(event.getParty());
        new ActionLogEntry("accept", accepter.getUniqueId(), new String[]{party.getLeader().getName()}).log();
    }
}