package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerServerSwitchListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

            if (party == null) {
                return;
            }

            if (!party.getLeader().equals(player.getUniqueId().toString())) {
                return;
            }

            RedisProvider.getRedisTransactions().runTransaction(jedis -> {
                jedis.publish("REDIS_PARTIES_CHANNEL", "BUNGEE%CONNECT%" + party.getUniqueId() + "%" + player.getServer().getInfo().getName());

                player.sendMessage(new ComponentBuilder("Los jugadores de tu party se estan moviendo a tu servidor.").color(ChatColor.LIGHT_PURPLE).create());
            });
        });
    }
}