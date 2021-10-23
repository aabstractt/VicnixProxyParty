package me.heyimblake.proxyparty.listeners;

import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import me.heyimblake.proxyparty.redis.RedisParty;
import me.heyimblake.proxyparty.redis.RedisProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ChatEvent ev) {
        ProxiedPlayer player = (ProxiedPlayer) ev.getSender();

        if (ev.isCommand() || ev.isCancelled()) return;

        if (!PartySetting.PARTY_CHAT_TOGGLE_ON.isEnabledFor(player)) {
            return;
        }

        String message = ev.getMessage();

        if (message.substring(0, 1).equalsIgnoreCase("!")) {
            ev.setMessage(message.substring(1));

            return;
        }

        ev.setCancelled(true);

        ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
            RedisParty party = RedisProvider.getInstance().getParty(player.getUniqueId());

            if (party == null) {
                return;
            }

            party.sendPartyMessage("PLAYER_CHAT%" + player.getUniqueId().toString() + "%" + message);
        });
    }
}