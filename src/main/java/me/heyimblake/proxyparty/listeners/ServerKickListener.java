package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onServerKickEvent(ServerKickEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

            if (party == null) {
                return;
            }

            party.sendPartyMessage("PLAYER_KICKED%" + player.getName());
        });
    }
}