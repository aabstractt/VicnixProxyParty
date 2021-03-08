package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
