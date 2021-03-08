package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
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
        name = "disband",
        syntax = "/party disband",
        description = "Deshacer tu party!.",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class DisbandSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No estas en una party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if(!party.getLeader().getName().equalsIgnoreCase(player.getName())){
            player.sendMessage(ChatColor.RED+"No puedes eliminar una party sin ser lider.");
            return;
        }

        party.disband();

        player.sendMessage(Constants.TAG, new ComponentBuilder("Has eliminado la party.").color(ChatColor.YELLOW).create()[0]);
    }
}
