package me.heyimblake.proxyparty.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.heyimblake.proxyparty.ProxyParty;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@AllArgsConstructor
@Data
@SuppressWarnings("deprecation")
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

    public void handlePlayerInvitedMessage(User user, User user0) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &einvito a %s &ea la party! Tiene &c%s &esegundos para &eaceptar.", ProxyParty.translatePrefix(user0), ProxyParty.translatePrefix(user), "60"))));
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }

    protected void handlePlayerJoinMessage(User user) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(ProxyParty.translatePrefix(user) + ChatColor.GREEN + " se ha unido a la party!");
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }

    protected void handlePlayerLeaveMessage(User user) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("%s &ese ha salido de la party.", ProxyParty.translatePrefix(user))));
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }

    public void handlePlayerDenyMessage(User user) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(ProxyParty.translatePrefix(user) + ChatColor.YELLOW + " ha rechazado la invitacion a tu party!");
        }
    }

    public void handlePlayerKickedMessage() {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(new ComponentBuilder("No se ha podido conectar a todos los miembros de la party al servidor actual").color(ChatColor.RED).create());
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }

    public void handlePartyWarpMessage() {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new ComponentBuilder("El lider de la party los ha movido a este servidor!").color(ChatColor.AQUA).create());
        }
    }

    public void handlePartyChatMessage(User user, String message) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&7[&bParty&7] &e%s: &7%s", ProxyParty.translatePrefix(user), ChatColor.stripColor(message))));
        }
    }

    protected void handlePartyTransferMessage(User user, User user0) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&eLa party fue transferida a %s&e por %s", ProxyParty.translatePrefix(user), ProxyParty.translatePrefix(user0)))));
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }

    protected void handlePartyStatusMessage(String status) {
        for (String uniqueId : this.members) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            player.sendMessage(ChatColor.GREEN + "La party ahora es " + ChatColor.GOLD + status);
            player.sendMessage(new TextComponent(ProxyParty.LINE));
        }
    }
}