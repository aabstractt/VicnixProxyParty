package me.heyimblake.proxyparty.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.heyimblake.proxyparty.events.PartySendInviteEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Data
public class RedisParty {

    private String uniqueId;
    private String leader;
    private Set<String> members;
    private Set<String> invited;
    private int maxMembers;

    public void invitePlayer(UUID targetUniqueId) {
        // TODO: insert into redis the invitation
    }

    public void sendPartyMessage(String message) {
        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            jedis.publish("REDIS_PARTIES_CHANNEL", "PARTY%" + this.uniqueId + "%" + message);
        });
    }

    public boolean isFull() {
        return this.maxMembers != -1 && this.members.size() >= this.maxMembers;
    }
}