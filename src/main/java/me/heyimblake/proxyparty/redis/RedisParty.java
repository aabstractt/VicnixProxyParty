package me.heyimblake.proxyparty.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.heyimblake.proxyparty.ProxyParty;

import java.util.*;

@AllArgsConstructor
@Data
public class RedisParty {

    private String uniqueId;
    private String leader;
    private Set<String> members;
    private Set<String> invited;
    private boolean partyPublic;
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

    public void setLeader() throws Exception {
        List<String> members = new ArrayList<>(this.members);

        Collections.sort(members);

        UUID uniqueId = UUID.fromString(members.get(0));
        String name = ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(uniqueId, true);

        if (name == null) {
            throw new Exception("Leader not found");
        }

        sendPartyMessage("PARTY_TRANSFER%" + uniqueId + "%" + this.leader);
        RedisProvider.getInstance().setPartyLeader(this.uniqueId, uniqueId);
    }

    public void setLeader(UUID uniqueId) {
        sendPartyMessage("PARTY_TRANSFER%" + uniqueId + "%" + this.leader);

        RedisProvider.getInstance().setPartyLeader(this.uniqueId, uniqueId);
    }
}