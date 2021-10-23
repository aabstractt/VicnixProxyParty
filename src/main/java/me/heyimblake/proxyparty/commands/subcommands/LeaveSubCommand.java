package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "salir",
        syntax = "/party salir",
        description = "Salir de una party!",
        requiresArgumentCompletion = false
)
public class LeaveSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party.getMembers().size() <= 2) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");

            return;
        }

        try {
            if (party.getLeader().equals(player.getUniqueId().toString())) {
                party.setLeader();
            }

            party.sendPartyMessage("PLAYER_LEAVE%" + player.getUniqueId().toString());

            RedisProvider.getInstance().removePartyMember(party.getUniqueId(), player.getUniqueId());
        } catch (Exception e) {
            party.disband(ChatColor.RED + "La party ha sido borrada debido a que no se encontro un lider");
        }
    }
}