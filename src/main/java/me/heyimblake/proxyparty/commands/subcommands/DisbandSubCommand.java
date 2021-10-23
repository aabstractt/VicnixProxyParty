package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(name = "disband", syntax = "/party disband", description = "Deshacer tu party!.", requiresArgumentCompletion = false, leaderExclusive = true)
public class DisbandSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(new ComponentBuilder("No estas en una party!").color(ChatColor.RED).create());

            return;
        }

        if (!party.getLeader().equals(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "No puedes eliminar una party sin ser lider.");

            return;
        }

        party.disband("PARTY_DISBAND%" + ProxyParty.getInstance().translatePrefix(player));

        player.sendMessage(new ComponentBuilder("Has eliminado la party.").color(ChatColor.YELLOW).create());
    }
}