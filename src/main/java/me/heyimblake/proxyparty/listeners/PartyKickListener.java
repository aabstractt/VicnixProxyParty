package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartyKickEvent;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PartyKickListener implements Listener {

    @EventHandler
    public void onPartyKick(PartyKickEvent ev) {
        ProxiedPlayer player = ev.getKickedPlayer();

        player.sendMessage(Constants.TAG, new ComponentBuilder("Fuiste kickeado de la party!").color(ChatColor.RED).bold(true).create()[0]);

        ProxyParty.getInstance().getMongo().updateParty(ev.getParty());
    }
}
