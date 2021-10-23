package me.heyimblake.proxyparty.commands.subcommands;

import me.heyimblake.proxyparty.commands.PartyAnnotationCommand;
import me.heyimblake.proxyparty.commands.PartySubCommand;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@PartyAnnotationCommand(name = "warp", syntax = "/party warp", description = "Envia todos los jugadores a tu servidor..", requiresArgumentCompletion = false, leaderExclusive = true)
public class WarpSubCommand extends PartySubCommand {

    @Override
    public void execute(ProxiedPlayer player, String[] args) {
        RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

        if (party == null) {
            return;
        }

        if (!party.getLeader().equals(player.getUniqueId().toString())) {
            return;
        }

        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            jedis.publish("REDIS_PARTIES_CHANNEL", "BUNGEE%CONNECT%" + party.getUniqueId() + "%" + player.getServer().getInfo().getName());
        });

        party.sendPartyMessage("PARTY_WARP");
    }
}