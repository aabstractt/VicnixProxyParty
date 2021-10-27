package me.heyimblake.proxyparty.redis;

import com.imaginarycode.minecraft.redisbungee.internal.jedis.Jedis;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPool;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.JedisPubSub;
import com.imaginarycode.minecraft.redisbungee.internal.jedis.Protocol;
import com.jaimemartz.playerbalancer.helper.PlayerLocker;
import lombok.Getter;
import me.heyimblake.proxyparty.ProxyParty;
import me.heyimblake.proxyparty.partyutils.PartySetting;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisProvider {

    public static String HASH_PLAYER_PARTY = "player#party:%s";
    public static String HASH_PARTY_MEMBERS = "party#members:%s";
    public static String HASH_PARTY_INVITED = "party#invited:%s";
    public static String HASH_PLAYER_PARTY_INVITES = "player#party#invites:%s";
    public static String HASH_PARTY_STATUS = "party#status";
    public static String HASH_PARTY_LEADER = "party#leader:%s";

    public static String HASH_PARTY_ANNOUNCE = "party#time";
    public static String HASH_PLAYER_ANNOUNCE = "player#time";

    @Getter
    private static final RedisProvider instance = new RedisProvider();

    @Getter
    protected static RedisTransactions redisTransactions;

    @Getter
    protected JedisPool jedisPool;

    protected JedisPubSub jedisPubSub;
    @Getter
    protected String password;

    public void init(String address, String password) {
        if (address == null) {
            throw new RuntimeException("Invalid redis data");
        }

        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.jedisPool = new JedisPool(host, port);

        Jedis jedis = this.jedisPool.getResource();

        if (password != null && !password.isEmpty()) {
            this.password = password;

            jedis.auth(this.password);
        }

        redisTransactions = new RedisTransactions(jedisPool, password);

        new Thread(() -> jedis.subscribe(this.jedisPubSub = new MessageListener(), "REDIS_PARTIES_CHANNEL")).start();
    }

    public RedisParty createParty(UUID uniqueId) {
        String partyUniqueId = UUID.randomUUID().toString();

        return redisTransactions.runTransaction(jedis -> {
            jedis.set(String.format(HASH_PLAYER_PARTY, uniqueId.toString()), partyUniqueId);
            jedis.set(String.format(HASH_PARTY_LEADER, partyUniqueId), uniqueId.toString());
            jedis.sadd(String.format(HASH_PARTY_MEMBERS, partyUniqueId), uniqueId.toString());

            return new RedisParty(partyUniqueId, uniqueId.toString(), new HashSet<>(), new HashSet<>(), false, -1);
        });
    }

    public void disbandParty(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            jedis.del(String.format(HASH_PARTY_LEADER, partyUniqueId));
            jedis.del(String.format(HASH_PLAYER_PARTY, uniqueId.toString()));
            jedis.del(String.format(HASH_PARTY_MEMBERS, partyUniqueId));
            jedis.del(String.format(HASH_PARTY_INVITED, partyUniqueId));
            jedis.srem(HASH_PARTY_ANNOUNCE, partyUniqueId);
            this.setPartyPublic(partyUniqueId, false);
        });
    }

    public void setPartyLeader(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            jedis.set(String.format(HASH_PARTY_LEADER, partyUniqueId), uniqueId.toString());
        });
    }

    public void setPartyPublic(String partyUniqueId, boolean isPublic) {
        redisTransactions.runTransaction(jedis -> {
            if (isPublic) {
                jedis.sadd(HASH_PARTY_STATUS, partyUniqueId);
            } else {
                jedis.srem(HASH_PARTY_STATUS, partyUniqueId);
            }
        });
    }

    public boolean isPartyPublic(String partyUniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            return jedis.sismember(HASH_PARTY_STATUS, partyUniqueId);
        });
    }

    public boolean isAlreadyInvited(UUID uniqueId, String partyUniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            return jedis.sismember(String.format(HASH_PLAYER_PARTY_INVITES, uniqueId.toString()), partyUniqueId);
        });
    }

    public RedisParty getParty(UUID uniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            String partyUniqueId = jedis.get(String.format(HASH_PLAYER_PARTY, uniqueId.toString()));

            if (partyUniqueId == null) {
                return null;
            }

            String leader = jedis.get(String.format(HASH_PARTY_LEADER, partyUniqueId));

            if (leader == null) {
                return null;
            }

            return new RedisParty(partyUniqueId, leader, getPartyMembers(partyUniqueId), getPartyInvited(partyUniqueId), isPartyPublic(partyUniqueId), -1);
        });
    }

    public RedisParty getParty(String partyUniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            String leader = jedis.get(String.format(HASH_PARTY_LEADER, partyUniqueId));

            if (leader == null) {
                return null;
            }

            return new RedisParty(partyUniqueId, leader, getPartyMembers(partyUniqueId), getPartyInvited(partyUniqueId), isPartyPublic(partyUniqueId), -1);
        });
    }

    public Set<String> getPartyMembers(String partyUniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            return jedis.smembers(String.format(HASH_PARTY_MEMBERS, partyUniqueId));
        });
    }

    public Set<String> getPartyInvited(String partyUniqueId) {
        return redisTransactions.runTransaction(jedis -> {
            return jedis.smembers(String.format(HASH_PARTY_INVITED, partyUniqueId));
        });
    }

    public void addPartyMember(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            String hash = String.format(HASH_PARTY_MEMBERS, partyUniqueId);

            if (jedis.sismember(hash, uniqueId.toString())) {
                return;
            }

            jedis.set(String.format(HASH_PLAYER_PARTY, uniqueId), partyUniqueId);

            jedis.sadd(hash, uniqueId.toString());
        });
    }

    public void addPartyInvite(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            String hash = String.format(HASH_PLAYER_PARTY_INVITES, uniqueId.toString());

            if (jedis.sismember(hash, partyUniqueId)) {
                return;
            }

            jedis.sadd(hash, partyUniqueId);

            hash = String.format(HASH_PARTY_INVITED, partyUniqueId);

            if (jedis.sismember(hash, uniqueId.toString())) {
                return;
            }

            jedis.sadd(hash, uniqueId.toString());
        });
    }

    public void removePartyMember(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            String hash = String.format(HASH_PARTY_MEMBERS, partyUniqueId);

            if (jedis.sismember(hash, uniqueId.toString())) {
                jedis.srem(hash, uniqueId.toString());
            }

            if (jedis.sismember(HASH_PLAYER_ANNOUNCE, uniqueId.toString())) {
                jedis.srem(HASH_PLAYER_ANNOUNCE, uniqueId.toString());
            }

            hash = String.format(HASH_PLAYER_PARTY, uniqueId);

            if (jedis.exists(hash)) {
                jedis.del(hash);
            }
        });
    }

    public void removePartyInvite(String partyUniqueId, UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            String hash = String.format(HASH_PARTY_INVITED, partyUniqueId);

            if (jedis.sismember(hash, uniqueId.toString())) {
                jedis.srem(hash, uniqueId.toString());
            }

            hash = String.format(HASH_PLAYER_PARTY_INVITES, uniqueId);

            if (jedis.sismember(hash, partyUniqueId)) {
                jedis.srem(hash, partyUniqueId);
            }
        });
    }

    public void removePartiesInvite(UUID uniqueId) {
        redisTransactions.runTransaction(jedis -> {
            String hash = String.format(HASH_PLAYER_PARTY_INVITES, uniqueId.toString());

            if (!jedis.exists(hash)) {
                return;
            }

            for (String partyUniqueId : jedis.smembers(hash)) {
                removePartyInvite(partyUniqueId, uniqueId);
            }

            jedis.del(hash);
        });
    }

    public void sendPlayerMessage(UUID uniqueId, String message) {
        redisTransactions.runTransaction(jedis -> {
            jedis.publish("REDIS_PARTIES_CHANNEL", "PLAYER%" + uniqueId.toString() + "%" + message);
        });
    }

    private void handlePartyMessage(RedisParty party, String[] args) {
        if (party == null) {
            return;
        }

        String type = args[0];

        if (type.equalsIgnoreCase("PLAYER_INVITED")) {
            party.handlePlayerInvitedMessage(ProxyParty.loadUser(args[1]), ProxyParty.loadUser(party.getLeader()));

            return;
        }

        if (type.equals("PLAYER_JOINED")) {
            party.handlePlayerJoinMessage(ProxyParty.loadUser(args[1]));

            return;
        }

        if (type.equals("PLAYER_LEAVE")) {
            party.handlePlayerLeaveMessage(ProxyParty.loadUser(args[1]));

            return;
        }

        if (type.equals("PARTY_DENIED")) {
            party.handlePlayerDenyMessage(ProxyParty.loadUser(args[1]));

            return;
        }

        if (type.equals("PLAYER_KICKED")) {
            party.handlePlayerKickedMessage();

            return;
        }

        if (type.equals("PARTY_WARP")) {
            party.handlePartyWarpMessage();

            return;
        }

        if (type.equals("PLAYER_CHAT")) {
            party.handlePartyChatMessage(ProxyParty.loadUser(args[1]), args[2]);

            return;
        }

        if (type.equals("PARTY_TRANSFER")) {
            party.handlePartyTransferMessage(ProxyParty.loadUser(args[1]), ProxyParty.loadUser(args[2]));

            return;
        }

        if (args[0].equals("PARTY_STATUS_UPDATE")) {
            party.handlePartyStatusMessage(args[1]);
        }
    }

    @SuppressWarnings("deprecation")
    private void handlePlayerMessage(String playerUniqueId, String[] args) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(playerUniqueId));

        if (player == null) {
            return;
        }

        if (args[0].equalsIgnoreCase("PARTY_INVITATION_RECEIVED")) {
            RedisParty party = getParty(args[1]);

            if (party == null) {
                return;
            }

            player.sendMessage(new TextComponent(ProxyParty.LINE));

            player.sendMessage(new TextComponent(" "));
            player.sendMessage(new ComponentBuilder("Has recibido una invitacion").color(ChatColor.AQUA).bold(true).create());

            User user = ProxyParty.loadUser(party.getLeader());

            String prefix = ProxyParty.translatePrefix(user);

            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("%s &ete ha invitado a unirte a su party!", prefix))));

            player.sendMessage(new TextComponent(" "));
            player.sendMessage(new ComponentBuilder("[ACEPTAR]").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party aceptar " + user.getUsername())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GRAY + "Click para aceptar la invitacion!")})).append(" - ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GRAY).append("[RECHAZAR]").color(ChatColor.RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party rechazar " + user.getUsername())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(ChatColor.GRAY + "Click para rechazar la invitacion")})).create());
            player.sendMessage(new TextComponent(" "));

            player.sendMessage(new TextComponent(ProxyParty.LINE));

            ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
                if (!player.isConnected()) {
                    return;
                }

                ProxyServer.getInstance().getScheduler().runAsync(ProxyParty.getInstance(), () -> {
                    RedisParty party0 = getParty(args[1]);

                    if (party0 == null) {
                        return;
                    }

                    if (!party0.getInvited().contains(playerUniqueId)) {
                        return;
                    }

                    player.sendMessage(ProxyParty.LINE);

                    player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', String.format("&eLa invitacion de party de %s &eha expirado!", prefix))));

                    player.sendMessage(ProxyParty.LINE);

                    removePartyInvite(party0.getUniqueId(), player.getUniqueId());

                    if ((party0.getMembers().size() - 1) <= 0) {
                        party0.disband("PARTY_DISBAND_PLAYERS");
                    }
                });
            }, 60, TimeUnit.SECONDS);

            return;
        }

        if (args[0].equals("PARTY_DISBAND")) {
            player.sendMessage(new TextComponent(ProxyParty.LINE));

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&a%s &eha borrado la party!", args[1])));

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            PartySetting.PARTY_CHAT_TOGGLE_ON.disable(player);

            return;
        }

        if (args[0].equals("PARTY_DISBAND_PLAYERS")) {
            player.sendMessage(new TextComponent(ProxyParty.LINE));

            player.sendMessage(ChatColor.RED + "La party ha sido borrada debido a la falta de jugadores");

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            PartySetting.PARTY_CHAT_TOGGLE_ON.disable(player);

            return;
        }

        if (args[0].equals("PARTY_KICK")) {
            player.sendMessage(new TextComponent(ProxyParty.LINE));

            player.sendMessage(ProxyParty.translatePrefix(UUID.fromString(args[1])) + ChatColor.RED + " te ha sacado de la party.");

            player.sendMessage(new TextComponent(ProxyParty.LINE));
            PartySetting.PARTY_CHAT_TOGGLE_ON.disable(player);

            return;
        }

        if (args[0].equals("PARTY_RETRACTED")) {
            player.sendMessage(new ComponentBuilder(String.format("%s ha removido tu invitacion de la party.", args[1])).color(ChatColor.GREEN).create());
        }
    }

    @SuppressWarnings("deprecation")
    private void handleMessage(String[] args) {
        if (args[0].equals("UPDATE")) {
            User user = ProxyParty.loadUser(args[1]);

            ProxyServer.getInstance().broadcast(new TextComponent(" "));
            ProxyServer.getInstance().broadcast(new TextComponent(ProxyParty.LINE));
            ProxyServer.getInstance().broadcast(new TextComponent(" "));
            ProxyServer.getInstance().broadcast(new ComponentBuilder("Has recibido una invitacion").color(ChatColor.AQUA).bold(true).create());
            ProxyServer.getInstance().broadcast(ProxyParty.translatePrefix(user) + ChatColor.YELLOW + " ha creado una party publica!");
            ProxyServer.getInstance().broadcast(new TextComponent(" "));
            ProxyServer.getInstance().broadcast(new ComponentBuilder("Utiliza ").color(ChatColor.YELLOW)
                    .append("/party join " + ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(user.getUniqueId(), true)).color(ChatColor.GREEN)
                    .append(" o da click").color(ChatColor.YELLOW)
                    .append(" AQUí ").color(ChatColor.AQUA).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + user.getUsername()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Da click para unirte a la party").create()))
                    .append("para entrar.", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW)
                    .create());
            //ProxyServer.getInstance().broadcast(ChatColor.YELLOW + "Utiliza " + ChatColor.GREEN + "/party join " + ProxyParty.getRedisBungee().getUuidTranslator().getNameFromUuid(user.getUniqueId(), true) + ChatColor.YELLOW + " o da click " + ChatColor.AQUA + ChatColor.BOLD + "AQUí" + ChatColor.RESET + ChatColor.YELLOW + " para entrar.");
            ProxyServer.getInstance().broadcast(new TextComponent(" "));
            ProxyServer.getInstance().broadcast(new TextComponent(ProxyParty.LINE));
            ProxyServer.getInstance().broadcast(new TextComponent(" "));

            return;
        }

        RedisParty party = getParty(args[1]);

        if (party == null) {
            return;
        }

        for (String uniqueId : party.getMembers()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

            if (player == null) {
                continue;
            }

            PlayerLocker.lock(player);
            player.connect(ProxyServer.getInstance().getServerInfo(args[2]));
        }

        ProxyServer.getInstance().getScheduler().schedule(ProxyParty.getInstance(), () -> {
            for (String uniqueId : party.getMembers()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(uniqueId));

                if (player == null) {
                    continue;
                }

                PlayerLocker.unlock(player);
            }
        }, 5L, TimeUnit.SECONDS);
    }

    public class MessageListener extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            new Thread(() -> {
                String[] args = message.split("%");

                if (args[0].equalsIgnoreCase("PLAYER")) {
                    handlePlayerMessage(args[1], Arrays.copyOfRange(args, 2, args.length));
                } else if (args[0].equals("PARTY")) {
                    handlePartyMessage(getParty(args[1]), Arrays.copyOfRange(args, 2, args.length));
                } else {
                    handleMessage(Arrays.copyOfRange(args, 1, args.length));
                }
            }).start();
        }
    }
}