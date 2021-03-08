package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.ActionLogEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) 2017 heyimblake
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author heyimblake
 * @since 10/21/2016
 */
@PartyAnnotationCommand(
        name = "publica",
        syntax = "/party publica",
        description = "Cambiar el estado de tú party a publica o privada",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class PublicSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party != null) {
            if (!party.getLeader().getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Debes ser lider la party!");

                return;
            }

            if (party.isPartyPublic()) {
                party.setPartyPublic(false);
                player.sendMessage(ChatColor.GREEN + "Ahora tu party es " + ChatColor.GOLD + "PRIVADA");
            } else {
                party.setPartyPublic(true);
                player.sendMessage(ChatColor.GREEN + "Ahora tu party es " + ChatColor.GOLD + "PUBLICA");
            }

        } else {
            player.sendMessage(ChatColor.RED + "Debes tener una party!");
        }
    }
}
