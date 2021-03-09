package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.partyutils.Party;
import me.heyimblake.proxyparty.partyutils.PartyManager;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "removerinvitacion",
        syntax = "/party removerinvitacion <Jugador>",
        description = "Remover la invitacion de un jugador.",
        requiresArgumentCompletion = true,
        leaderExclusive = true
)
public class RetractSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (CommandConditions.checkTargetOnline(target, player)) return;

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (!party.getInvited().contains(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("%s no ha sido invitado a tu party.", target.getName())).color(ChatColor.RED).create()[0]);

            return;
        }

        party.retractInvite(target);

        player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("Haz removido la invitación " +
                "de la party al jugador %s.", target.getName())).color(ChatColor.GREEN).create()[0]);

        target.sendMessage(Constants.TAG, new ComponentBuilder(String.format("%s ha removido tu invitacion de la party.",
                player.getName())).color(ChatColor.GREEN).create()[0]);

        if (party.getInvited().size() <= 0 && party.getParticipants().size() <= 0) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");
        }
    }
}