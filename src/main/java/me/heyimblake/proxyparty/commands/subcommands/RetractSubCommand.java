package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.CommandConditions;
import me.heyimblake.proxyparty.utils.Constants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

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
        UUID targetUniqueId = ProxyParty.getRedisBungee().getUuidTranslator().getTranslatedUuid(args[0], true);

        if (CommandConditions.checkTargetOnline(targetUniqueId, player)) {
            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (!party.getInvited().contains(targetUniqueId.toString())) {
            player.sendMessage(Constants.TAG, new ComponentBuilder(String.format("%s no ha sido invitado a tu party.", args[0])).color(ChatColor.RED).create()[0]);

            return;
        }

        RedisProvider.getInstance().removePartyInvite(party.getUniqueId(), targetUniqueId);

        player.sendMessage(new ComponentBuilder(String.format("Haz removido la invitación de la party al jugador %s.", ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(targetUniqueId, true))).color(ChatColor.GREEN).create());

        RedisProvider.getInstance().sendPlayerMessage(targetUniqueId, "PARTY_RETRACTED%" + ProxyParty.getInstance().translatePrefix(player));

        if ((party.getInvited().size() - 1) <= 0 && party.getMembers().size() <= 1) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");
        }
    }
}