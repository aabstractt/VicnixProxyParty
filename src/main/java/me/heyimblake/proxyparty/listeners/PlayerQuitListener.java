package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            RedisProvider.getInstance().removePartiesInvite(player.getUniqueId());

            RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

            if (party == null) {
                return;
            }

            RedisProvider.getInstance().removePartyMember(party.getUniqueId(), player.getUniqueId());
            party.getMembers().remove(player.getUniqueId().toString());

            if (party.getMembers().size() <= 1) {
                party.disband("PARTY_DISBAND_PLAYERS");

                return;
            }

            try {
                if (party.getLeader().equals(player.getUniqueId().toString())) {
                    party.setLeader();
                }

                party.sendPartyMessage("PLAYER_LEAVE%" + player.getUniqueId().toString());
            } catch (Exception e) {
                party.disband("PARTY_DISBAND_PLAYERS");
            }
        });
    }
}