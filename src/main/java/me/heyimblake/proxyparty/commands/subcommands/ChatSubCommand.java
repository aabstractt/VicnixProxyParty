package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.utils.ActionLogEntry;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

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
        name = "chat",
        syntax = "/party chat (mensaje)",
        description = "Envia un mensaje a los miembros de la party.",
        requiresArgumentCompletion = false
)
public class ChatSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)
                    ? "Todos los mensajes que se envien se enviaran a la party."
                    : "Los mensajes ya no se enviaran a la party!.")
                    .color(!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player) ? ChatColor.GREEN : ChatColor.RED).create()[0]);

            if (PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)) {
                PartySetting.PARTY_CHAT_TOGGLE_ON.disable(player);
            } else {
                PartySetting.PARTY_CHAT_TOGGLE_ON.enable(player);
            }

            return;
        }

        final String[] message = {""};

        Arrays.stream(args).forEach(string -> message[0] += string + " ");

        PartyManager.getInstance().getPartyOf(player).sendPartyMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7[&bParty&7] &e%s: &7%s", player.getName(), message[0])));

        new ActionLogEntry("chat", player.getUniqueId(), new String[]{message[0]}).log();
    }
}
