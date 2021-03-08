package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartySendInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

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
 * @since 10/23/2016
 */
public class PartySendInviteListener implements Listener {

    @EventHandler
    public void onPartySendInvite(PartySendInviteEvent event) {
        ProxiedPlayer player = event.getInvited();
        ProxiedPlayer inviter = event.getInviter();

        TextComponent text = new TextComponent(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------");

        player.sendMessage(text);

        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &ate ha invitado unirte a su party!", inviter.getName()))));
        player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format("&aTienes &c%s&a segundos para aceptar. &6Click aquí para unirte!", "60")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + inviter.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click para ejecutar\n/party accept " + inviter.getName())})).create()[0]);

        player.sendMessage(text);

        ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
            if (!inviter.isConnected() || !player.isConnected()) return;

            Party party = PartyManager.getInstance().getPartyOf(inviter);

            if (party == null || !party.getLeader().getUniqueId().equals(inviter.getUniqueId())) return;

            if (!party.getInvited().contains(player)) return;

            player.sendMessage(text);

            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&aLa invitacion de party de &d%s &aha expirado!", inviter.getName()))));

            player.sendMessage(text);

            party.getInvited().remove(player);

            if (party.getInvited().size() <= 0 && party.getParticipants().size() <= 0) {
                party.disband();
            }
        }, 10, TimeUnit.SECONDS);
    }
}