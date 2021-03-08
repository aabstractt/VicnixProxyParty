package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyKickEvent;
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
 * @since 10/21/2016
 */
@PartyAnnotationCommand(
        name = "kick",
        syntax = "/party kick <Jugador>",
        description = "Kickear a un jugador de la party",
        requiresArgumentCompletion = true,
        leaderExclusive = true
)
public class KickSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (!CommandConditions.checkTargetOnline(target, player)) return;

        Party targetParty = PartyManager.getInstance().getPartyOf(target);

        if (targetParty == null || targetParty != party) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador no esta en tu party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if (party.getLeader().getUniqueId().equals(target.getUniqueId()) || target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No puedes kickearte a ti mismo o al lider de la party.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.removeParticipant(target);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyKickEvent(party, target));

        player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("Haz kickeado a %s de tu party!!", target.getName())).color(ChatColor.YELLOW).create()[0]);

        if (party.getParticipants().size() <= 0) {
            party.disband();
        }
    }
}
