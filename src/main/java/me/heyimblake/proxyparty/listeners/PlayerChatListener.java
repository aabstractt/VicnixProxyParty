package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (event.isCommand() || event.isCancelled()) return;

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (party == null) {
            return;
        }

        if (!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)) {
            return;
        }

        if (event.getMessage().substring(0, 1).equalsIgnoreCase("!")) {
            event.setMessage(event.getMessage().substring(1));

            return;
        }

        event.setCancelled(true);

        party.sendPartyMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7[&bParty&7] &e%s: &7%s", player.getName(), event.getMessage())));
    }
}