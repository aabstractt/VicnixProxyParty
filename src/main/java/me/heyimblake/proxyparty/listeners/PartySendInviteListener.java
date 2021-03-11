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
    public void onPartySendInvite(PartySendInviteEvent ev) {
        ProxiedPlayer player = ev.getInvited();
        ProxiedPlayer inviter = ev.getInviter();

        TextComponent text = new TextComponent(Constants.LINE);

        player.sendMessage(text);

        BaseComponent[] clickMessages = new ComponentBuilder("[ACEPTAR]").color(ChatColor.GREEN).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party aceptar " + inviter.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GRAY + "Click para aceptar la invitacion!")}))
                .append(" - ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY)
                .append("[RECHAZAR]").color(ChatColor.RED).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party rechazar " + inviter.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GRAY + "Click para rechazar la invitacion")})).create();

        player.sendMessage(new TextComponent(" "));
        player.sendMessage(new ComponentBuilder("Has recibido una invitacion").color(ChatColor.AQUA).bold(true).create()[0]);
        player.sendMessage(new ComponentBuilder(String.format("%s te ha invitado a unirte a su party!",
                inviter.getName())).color(ChatColor.YELLOW).create()[0]);
        player.sendMessage(new TextComponent(" "));
        player.sendMessage(clickMessages[0], clickMessages[1], clickMessages[2]);
        player.sendMessage(new TextComponent(" "));

        player.sendMessage(text);

        ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
            if (!inviter.isConnected() || !player.isConnected()) return;

            Party party = PartyManager.getInstance().getPartyOf(inviter);

            if (party == null || !party.getLeader().getUniqueId().equals(inviter.getUniqueId())) return;

            if (!party.getInvited().contains(player)) return;

            player.sendMessage(text);

            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&eLa invitacion de party de &a%s &eha expirado!", inviter.getName()))));

            player.sendMessage(text);

            party.getInvited().remove(player);

            if (party.getInvited().size() <= 0 && party.getParticipants().size() <= 0) {
                party.disband(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");
            }
        }, 60, TimeUnit.SECONDS);
    }
}