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
        name = "forcejoin",
        description = "Forzar la entrada a una party",
        mustBeInParty = false,
        syntax = "/party forcejoin <player>",
        requiresArgumentCompletion = true,
        showHelp = false
)
public class ForceJoinSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (!ProxyParty.isDev(player)) {
            player.sendMessage(ChatColor.RED + "No tienes permisos de ejecutar esté comando.");

            return;
        }

        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(targetUniqueId);

        if (party == null) {
            player.sendMessage(new ComponentBuilder("El jugador especificado no se encuentra en una party.").color(ChatColor.RED).create());

            return;
        }

        RedisProvider.getInstance().removePartiesInvite(player.getUniqueId());
        RedisProvider.getInstance().addPartyMember(party.getUniqueId(), player.getUniqueId());

        party.sendPartyMessage("PLAYER_JOINED%" + player.getUniqueId().toString());

        player.sendMessage(new ComponentBuilder(String.format("Has ingresado a la %s's Party!!", ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(targetUniqueId, true))).color(ChatColor.GREEN).create());
    }
}