package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

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
        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

            if (CommandConditions.checkTargetOnline(targetUniqueId, player)) {
                return;
            }

            RedisParty party = RedisProvider.getInstance().getParty(targetUniqueId);

            if (party == null || !party.getLeader().equals(targetUniqueId.toString())) {
                player.sendMessage(new ComponentBuilder("El jugador a la que le intentaste rechazar la party no esta en una o no esta conectado").color(ChatColor.RED).create());

                return;
            }

            if (!party.getInvited().contains(player.getUniqueId().toString())) {
                player.sendMessage(Constants.TAG, new ComponentBuilder("No tienes invitacion a esta party.").color(ChatColor.RED).create()[0]);

                return;
            }

            RedisProvider.getInstance().removePartyInvite(party.getUniqueId(), player.getUniqueId());

            party.sendPartyMessage("PARTY_DENIED%" + player.getUniqueId());

            player.sendMessage(new ComponentBuilder(String.format("Has declinado la invitacion de la party de %s!.", ProxyParty.getInstance().translatePrefix(targetUniqueId))).color(ChatColor.GREEN).create());
        });
    }
}