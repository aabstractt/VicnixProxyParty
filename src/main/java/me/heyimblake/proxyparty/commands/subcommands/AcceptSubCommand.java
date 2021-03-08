package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyAcceptInviteEvent;
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
        name = "aceptar",
        syntax = "/party aceptar <Jugador>",
        description = "Acepta la invitacion de una party.",
        requiresArgumentCompletion = true,
        mustBeInParty = false
)
public class AcceptSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (!CommandConditions.checkTargetOnline(target, player)) return;

        if (!CommandConditions.blockIfHasParty(player)) return;

        Party party = PartyManager.getInstance().getPartyOf(target);

        if (party == null || !party.getLeader().getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(
                    "El jugador especificado o la party a la que te intentas unir ya no existe o se ha expirado la invitacion")
                    .color(ChatColor.RED).create()[0]);
            return;
        }

        if (party.getInvited().contains(player)) {
            if (Constants.MAX_PARTY_SIZE <= party.getParticipants().size()
                    && Constants.MAX_PARTY_SIZE != -1) {
                player.sendMessage(Constants.TAG, new ComponentBuilder(
                        "La party a la que te intentas unir esta totalmente llena!")
                        .color(ChatColor.RED).bold(true).create()[0]);
                return;
            }

            party.addParticipant(player);
            party.getInvited().remove(player);

            ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyAcceptInviteEvent(party, player));

            return;
        }

        player.sendMessage(Constants.TAG, new ComponentBuilder("No tienes invitaciones para unirte a esta party!").color(ChatColor.RED).create()[0]);
    }
}
