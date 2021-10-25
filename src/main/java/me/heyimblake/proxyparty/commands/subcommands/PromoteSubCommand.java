package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

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
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (!party.getMembers().contains(targetUniqueId.toString())) {
            player.sendMessage(new ComponentBuilder("Este jugador no esta en tu party!").color(ChatColor.RED).create()[0]);

            return;
        }

        if (targetUniqueId.equals(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("No puedes transferir la party a ti mismo.").color(ChatColor.RED).create()[0]);

            return;
        }

        party.setLeader(targetUniqueId);
    }
}