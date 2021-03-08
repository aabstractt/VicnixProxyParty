package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.utils.ActionLogEntry;
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
 * @since 11/28/2016
 */
@PartyAnnotationCommand(
        name = "toggle",
        syntax = "/party toggle <Chat o Invitaciones>",
        description = "Desactivar/Activar las configuraciones..",
        requiresArgumentCompletion = true,
        mustBeInParty = false)
public class ToggleSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        String settingString = args[0];

        PartySetting partySetting = PartySetting.getPartySetting(settingString);

        if (partySetting == null) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No puedes editar esta configuración ya que no existe, configuraciones disponibles: ")
                    .color(ChatColor.RED).create()[0]);
            sendAllSettings(player);

            return;
        }

        partySetting.toggle(player);

        TextComponent msg = new TextComponent(partySetting.getNiceName() + " ha sido a cambiado a ");

        msg.setColor(ChatColor.YELLOW);

        TextComponent msg2 = new TextComponent(partySetting.isEnabledFor(player) ? " activado." : " desactivado.");

        msg2.setColor(ChatColor.GREEN);

        player.sendMessage(Constants.TAG, msg, msg2);

        new ActionLogEntry("toggle", player.getUniqueId(), new String[]{settingString}).log();
    }

    private void sendAllSettings(ProxiedPlayer player) {
        String str = "";

        for (PartySetting partySetting : PartySetting.values()) {
            str += partySetting.getArgumentString() + ", ";
        }

        TextComponent msg = new TextComponent(str);

        msg.setColor(ChatColor.WHITE);

        player.sendMessage(Constants.TAG, msg);
    }
}
