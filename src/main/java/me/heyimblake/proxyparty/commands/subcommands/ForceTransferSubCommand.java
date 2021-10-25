package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "forcetransfer",
        description = "Forzar la transferencia de una party",
        mustBeInParty = false,
        syntax = "/party forcetransfer",
        requiresArgumentCompletion = false,
        showHelp = false
)
public class ForceTransferSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        if (!ProxyParty.isDev(player)) {
            player.sendMessage(new ComponentBuilder("No tienes permisos de ejecutar esté comando.").color(ChatColor.RED).create());

            return;
        }

        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(new ComponentBuilder("Necesitas estar en una party para ejecutar esté comando.").color(ChatColor.RED).create());

            return;
        }

        if (party.getLeader().equals(player.getUniqueId().toString())) {
            player.sendMessage(new ComponentBuilder("Ya eres el lider de está party").color(ChatColor.RED).create());

            return;
        }

        party.setLeader(player.getUniqueId());
    }
}
