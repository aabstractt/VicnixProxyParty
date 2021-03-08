package me.heyimblake.proxyparty.utils;

import me.heyimblake.proxyparty.partyutils.PartyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandConditions {

    public static Boolean checkTargetOnline(ProxiedPlayer proxiedPlayer, ProxiedPlayer sender) {
        if (proxiedPlayer == null) {
            sender.sendMessage(Constants.TAG, new ComponentBuilder("El jugador no se pudo encontrar.").color(ChatColor.RED).create()[0]);

            return true;
        }

        return false;
    }

    public static Boolean blockIfHasParty(ProxiedPlayer sender) {
        if (PartyManager.getInstance().hasParty(sender)) {
            sender.sendMessage(Constants.TAG, new ComponentBuilder("Estas ya en una party!").color(ChatColor.RED).create()[0]);

            return true;
        }

        return false;
    }
}