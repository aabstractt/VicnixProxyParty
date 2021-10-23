package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(
        name = "publica",
        syntax = "/party publica",
        description = "Cambiar el estado de tú party a publica o privada",
        requiresArgumentCompletion = false,
        leaderExclusive = true
)
public class PublicSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(ChatColor.RED + "Debes tener una party!");

            return;
        }

        if (!party.getLeader().equals(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "Debes ser lider la party!");

            return;
        }

        RedisProvider.getInstance().setPartyPublic(party.getUniqueId(), !party.isPartyPublic());

        party.sendPartyMessage("PARTY_STATUS_UPDATE%" + (party.isPartyPublic() ? "PRIVADA" : "PUBLICA"));

        if (party.isPartyPublic()) {
            return;
        }

        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            jedis.publish("REDIS_PARTIES_CHANNEL", "BUNGEE%UPDATE%" + ProxyParty.getInstance().translatePrefix(player));
        });
    }
}