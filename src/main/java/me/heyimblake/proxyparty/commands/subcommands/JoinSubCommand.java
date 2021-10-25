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
        name = "entrar",
        syntax = "/party entrar <Jugador>",
        description = "Entrar a una party si es publica.",
        requiresArgumentCompletion = true,
        mustBeInParty = false
)
public class JoinSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        if (RedisProvider.getInstance().getParty(player.getUniqueId()) != null) {
            player.sendMessage(new ComponentBuilder("Ya te encuentras en una party!").color(ChatColor.RED).create());

            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(targetUniqueId);

        if (party == null || !party.getLeader().equals(targetUniqueId.toString())) {
            player.sendMessage(new ComponentBuilder(
                    "No puedes acceder a esta party no existe o tú la creaste.")
                    .color(ChatColor.RED).create());

            return;
        }

        if (party.isFull()) {
            player.sendMessage(new ComponentBuilder(
                    "La party a la que te intentas unir esta totalmente llena!")
                    .color(ChatColor.RED).bold(true).create());
            return;
        }

        if (!party.isPartyPublic()) {
            player.sendMessage(ChatColor.RED + "¡Está party no esta publica!");

            return;
        }

        RedisProvider.getInstance().removePartiesInvite(player.getUniqueId());
        RedisProvider.getInstance().addPartyMember(party.getUniqueId(), player.getUniqueId());

        party.sendPartyMessage("PLAYER_JOINED%" + player.getUniqueId().toString());

        player.sendMessage(new ComponentBuilder(String.format("Has ingresado a la %s's Party!!", ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(targetUniqueId, true))).color(ChatColor.GREEN).create());
    }
}