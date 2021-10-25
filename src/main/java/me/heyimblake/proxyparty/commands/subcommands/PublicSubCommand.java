package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(name = "publica", syntax = "/party publica", description = "Cambiar el estado de tú party a publica o privada", requiresArgumentCompletion = false, leaderExclusive = true)
public class PublicSubCommand extends PartySubCommand {

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            player.sendMessage(ChatColor.RED + "Debes tener una party!");

            return;
        }

        String uniqueId = player.getUniqueId().toString();

        if (!party.getLeader().equals(uniqueId)) {
            player.sendMessage(ChatColor.RED + "Debes ser lider la party!");

            return;
        }

        RedisProvider.getInstance().setPartyPublic(party.getUniqueId(), !party.isPartyPublic());

        party.sendPartyMessage("PARTY_STATUS_UPDATE%" + (party.isPartyPublic() ? "PRIVADA" : "PUBLICA"));

        if (party.isPartyPublic()) {
            return;
        }

        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            if (!ProxyParty.canAnnounce(player)) {
                if (jedis.sismember(RedisProvider.HASH_PARTY_ANNOUNCE, party.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Tienes que esperar " + jedis.ttl(RedisProvider.HASH_PARTY_ANNOUNCE + ":" + party.getUniqueId()) + " segundos para que tu party se anuncie en global.");

                    return;
                }

                if (jedis.sismember(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId)) {
                    player.sendMessage(ChatColor.RED + "Tienes que esperar " + jedis.ttl(RedisProvider.HASH_PLAYER_ANNOUNCE + ":" + uniqueId) + " segundos para que tu party se anuncie en global.");

                    return;
                }
            }

            jedis.sadd(RedisProvider.HASH_PARTY_ANNOUNCE, party.getUniqueId());
            jedis.expire(RedisProvider.HASH_PARTY_ANNOUNCE + ":" + party.getUniqueId(), 25);

            jedis.sadd(RedisProvider.HASH_PLAYER_ANNOUNCE, uniqueId);
            jedis.expire(RedisProvider.HASH_PLAYER_ANNOUNCE + ":" + uniqueId, 25);

            jedis.publish("REDIS_PARTIES_CHANNEL", "BUNGEE%UPDATE%" + uniqueId);
        });
    }
}