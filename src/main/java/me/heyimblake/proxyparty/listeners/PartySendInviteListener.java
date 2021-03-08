package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.events.PartySendInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PartySendInviteListener implements Listener {

    @EventHandler
    public void onPartySendInvite(PartySendInviteEvent event) {
        ProxiedPlayer player = event.getInvited();
        ProxiedPlayer inviter = event.getInviter();

        TextComponent text = new TextComponent(Constants.LINE);

        player.sendMessage(text);

        player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&d%s &ate ha invitado unirte a su party!", inviter.getName()))));
        player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', String.format("&aTienes &c%s&a segundos para aceptar. &6Click aquí para unirte!", "60")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + inviter.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click para ejecutar\n/party accept " + inviter.getName())})).create()[0]);

        player.sendMessage(text);

        ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
            if (!inviter.isConnected() || !player.isConnected()) return;

            Party party = PartyManager.getInstance().getPartyOf(inviter);

            if (party == null || !party.getLeader().getUniqueId().equals(inviter.getUniqueId())) return;

            if (!party.getInvited().contains(player)) return;

            player.sendMessage(text);

            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&aLa invitacion de party de &d%s &aha expirado!", inviter.getName()))));

            player.sendMessage(text);

            party.getInvited().remove(player);

            if (party.getInvited().size() <= 0 && party.getParticipants().size() <= 0) {
                party.disband();
            }
        }, 10, TimeUnit.SECONDS);
    }
}