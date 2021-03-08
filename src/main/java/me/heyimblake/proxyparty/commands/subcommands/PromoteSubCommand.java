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
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "promote",
        syntax = "/party promote <Jugador>",
        description = "Transferir el propietario de la party a un jugador de la party",
        requiresArgumentCompletion = true,
        leaderExclusive = true
)
public class PromoteSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        Party party = PartyManager.getInstance().getPartyOf(player);

        if (!CommandConditions.checkTargetOnline(target, player)) return;

        if (!party.isParticipant(target)) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("Este jugador no esta en tu party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder("No puedes transferir la party a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.setLeader(target);
    }
}
