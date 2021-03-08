package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyDenyInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
@PartyAnnotationCommand(
        name = "rechazar",
        syntax = "/party rechazar <Jugador>",
        description = "Rechazar la party de un jugador.",
        requiresArgumentCompletion = true,
        mustBeInParty = false
)
public class DenySubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (!CommandConditions.blockIfHasParty(player)) return;

        if (PartyManager.getInstance().getPartyOf(target) == null || !PartyManager.getInstance().getPartyOf(target).getLeader().getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("" +
                    "El jugador a la que le intentaste rechazar la party no esta en una o no esta conectado").color(ChatColor.RED).create()[0]);
            return;
        }

        Party party = PartyManager.getInstance().getPartyOf(target);

        if (!party.getInvited().contains(player)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No tienes invitacion a esta party.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.getInvited().remove(player);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyDenyInviteEvent(party, player));

        player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("Has declinado la invitacion de la party de %s!.", target.getName())).color(ChatColor.GREEN).create()[0]);
    }
}
