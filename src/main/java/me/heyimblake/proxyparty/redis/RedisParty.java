package me.heyimblake.proxyparty.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

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

    public void sendPartyMessage(String message) {
        RedisProvider.getRedisTransactions().runTransaction(jedis -> {
            jedis.publish("REDIS_PARTIES_CHANNEL", "PARTY%" + this.uniqueId + "%" + message);
        });
    }

    public void disband(String message) {
        for (String uniqueId : this.members) {
            RedisProvider.getInstance().sendPlayerMessage(UUID.fromString(uniqueId), message);
        }

        RedisProvider.getInstance().disbandParty(this.uniqueId, UUID.fromString(this.leader));
    }

    public boolean isFull() {
        return this.maxMembers != -1 && this.members.size() >= this.maxMembers;
    }
}