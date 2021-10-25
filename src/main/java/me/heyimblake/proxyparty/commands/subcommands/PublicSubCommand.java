package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

@PartyAnnotationCommand(
        name = "publica",
        syntax = "/party publica",
        description = "Cambiar el estado de tú party a publica o privada",
        requiresArgumentCompletion = false,
        mustBeInParty = false
)
public class PublicSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            party = RedisProvider.getInstance().createParty(player.getUniqueId());
        }

        String uniqueId = player.getUniqueId().toString();

        if (!party.getLeader().equals(uniqueId)) {
            player.sendMessage(ChatColor.RED + "Debes ser lider la party!");

            return;
        }

        String partyUniqueId = party.getUniqueId();

        RedisProvider.getInstance().setPartyPublic(partyUniqueId, !party.isPartyPublic());

        party.sendPartyMessage("PARTY_STATUS_UPDATE%" + (party.isPartyPublic() ? "PRIVADA" : "PUBLICA"));

        if (party.isPartyPublic()) {
            return;
        }

        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            if (!ProxyParty.canAnnounce(player)) {
                if (jedis.sismember(RedisProvider.HASH_PARTY_ANNOUNCE, partyUniqueId)) {
                    player.sendMessage(ChatColor.RED + "Tienes que esperar 25 segundos para que tu party se anuncie en global.");

                    return;
                }

                if (jedis.sismember(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId)) {
                    player.sendMessage(ChatColor.RED + "Tienes que esperar 25 segundos para que tu party se anuncie en global.");

                    return;
                }
            }

            jedis.sadd(RedisProvider.HASH_PARTY_ANNOUNCE, partyUniqueId);
            jedis.sadd(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId);

            jedis.publish("REDIS_PARTIES_CHANNEL", "BUNGEE%UPDATE%" + uniqueId);

            // I love u jedis
            ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
                if (jedis.sismember(RedisProvider.HASH_PARTY_ANNOUNCE, partyUniqueId)) {
                    jedis.srem(RedisProvider.HASH_PARTY_ANNOUNCE, partyUniqueId);
                }

                if (jedis.sismember(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId)) {
                    jedis.srem(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId);
                }
            }, 25, TimeUnit.SECONDS);
        });
    }
}