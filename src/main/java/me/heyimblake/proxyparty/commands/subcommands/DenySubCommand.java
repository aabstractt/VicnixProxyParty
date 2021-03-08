package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.*;
import me.heyimblake.proxyparty.events.PartyDenyInviteEvent;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "rechazar",
        syntax = "/party rechazar <Jugador>",
        description = "Rechazar la party de un jugador.",
        requiresArgumentCompletion = true,
        mustBeInParty = false
)
public class DenySubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (CommandConditions.blockIfHasParty(player)) return;

        if (PartyManager.getInstance().getPartyOf(target) == null || !PartyManager.getInstance().getPartyOf(target).getLeader().getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("" +
                    "El jugador a la que le intentaste rechazar la party no esta en una o no esta conectado").color(ChatColor.RED).create()[0]);
            return;
        }

        Party party = PartyManager.getInstance().getPartyOf(target);

        if (!party.getInvited().contains(player)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No tienes invitacion a esta party.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.getInvited().remove(player);

        ProxyParty.getInstance().getProxy().getPluginManager().callEvent(new PartyDenyInviteEvent(party, player));

        player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("Has declinado la invitacion de la party de %s!.", target.getName())).color(ChatColor.GREEN).create()[0]);
    }
}
