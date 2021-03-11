package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyCreator;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PartyAnnotationCommand(
        name = "invite",
        syntax = "/party invite <Player>",
        description = "Invitar un jugador a tu party!",
        requiresArgumentCompletion = true,
        leaderExclusive = true,
        mustBeInParty = false)
public class InviteSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyParty.getInstance().getProxy().getPlayer(args[0]);

        if (target == null || target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No se puedes invitar este jugador a la party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if(target.getServer().getInfo().getName().contains("Auth")){
            player.sendMessage(ChatColor.RED + "El jugador no ha iniciado sesión no puedes invitarlo.");

            return;
        }

        Party party = !PartyManager.getInstance().hasParty(player) ? new PartyCreator().setLeader(player).create() : PartyManager.getInstance().getPartyOf(player);

        if (party.getInvited().contains(target) || party.isParticipant(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador ya fue invitado a tu party!!").color(ChatColor.RED).create()[0]);
            return;
        }

        if (PartySetting.PARTY_INVITE_RECEIVE_TOGGLE_OFF.getPlayers().contains(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador tiene desactivadas la invitaciones a una party!.").color(ChatColor.RED).create()[0]);
            return;
        }

        if (party.getAllParticipants().size() >= party.getMax()) {
            BaseComponent[] message = new ComponentBuilder("Tu party esta totalmente llena, compra un rango mas superior en").color(ChatColor.RED)
                    .append("\n tienda.vincix.net ").color(ChatColor.GREEN)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://tienda.vicnix.net"))
                    .append("para tener mas slots de party!").color(ChatColor.RED).create();

            player.sendMessage(message[0], message[1], message[2]);

            return;
        }

        party.invitePlayer(target);

        party.sendPartyMessage(new TextComponent(Constants.LINE));
        party.sendPartyMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &einvito a &6%s&e a la party! Tiene &c%s&e segundos para &eaceptar.", player.getName(), target.getName(), "60"))));
        party.sendPartyMessage(new TextComponent(Constants.LINE));
    }

    @Override
    public List<String> getComplete(String[] args) {
        List<String> complete = new ArrayList<>();

        String name = args[0];

        int lastSpaceIndex = name.lastIndexOf(' ');

        if (lastSpaceIndex >= 0) {
            name = name.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (!player.getName().toLowerCase().startsWith(name.toLowerCase())) {
                continue;
            }

            if (complete.contains(player.getName())) continue;

            complete.add(player.getName());
        }

        Collections.sort(complete);

        return complete;
    }
}