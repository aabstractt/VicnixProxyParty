package me.heyimblake.proxyparty;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import lombok.Getter;
import me.heyimblake.proxyparty.commands.PartyCommand;
import me.heyimblake.proxyparty.listeners.PlayerChatListener;
import me.heyimblake.proxyparty.listeners.PlayerQuitListener;
import me.heyimblake.proxyparty.listeners.PlayerServerSwitchListener;
import me.heyimblake.proxyparty.listeners.ServerKickListener;
import me.heyimblake.proxyparty.redis.RedisProvider;
import me.heyimblake.proxyparty.utils.ConfigManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class ProxyParty extends Plugin {

    public static String LINE = ChatColor.LIGHT_PURPLE + ChatColor.STRIKETHROUGH.toString() + "--------------------------------";

    @Getter
    private static ProxyParty instance;

    @Getter
    private LuckPerms luckPerms;
    @Getter
    private static RedisBungee redisBungee;

    private ConfigManager configManager;

    public final static PartyCommand command = new PartyCommand();

    @Override
    public void onEnable() {
        instance = this;

        //this.getProxy().getPluginManager().registerCommand(this, new PartyCommand());

        this.registerListeners();

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            e.printStackTrace();

            this.getProxy().stop();

            return;
        }

        this.configManager = new ConfigManager();

        this.configManager.initialize();

        RedisProvider.getInstance().init(getConfig().getString("redis.host"), getConfig().getString("redis.password"));

        redisBungee = (RedisBungee) getProxy().getPluginManager().getPlugin("RedisBungee");
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new PlayerChatListener());
        getProxy().getPluginManager().registerListener(this, new PlayerQuitListener());
        getProxy().getPluginManager().registerListener(this, new PlayerServerSwitchListener());
        getProxy().getPluginManager().registerListener(this, new ServerKickListener());
    }

    public static User loadUser(String uniqueId) {
        return loadUser(UUID.fromString(uniqueId));
    }

    public static User loadUser(UUID uuid) {
        try {
            return instance.luckPerms.getUserManager().loadUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static User loadUser(ProxiedPlayer player) {
        return instance.luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(player);
    }

    public static String translatePrefix(ProxiedPlayer player) {
        return translatePrefix(loadUser(player));
    }

    public static String translatePrefix(UUID uuid) {
        User user = loadUser(uuid);

        if (user == null) {
            return "";
        }

        return translatePrefix(user);
    }

    public static String translatePrefix(User user) {
        String prefix = user.getCachedData().getMetaData().getPrefix();

        return (prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) + " " : ChatColor.GRAY) + redisBungee.getUuidTranslator().getNameFromUuid(user.getUniqueId(), true);
    }

    public static boolean canAnnounce(ProxiedPlayer player) {
        return loadUser(player).getPrimaryGroup().equals("dev") || player.hasPermission("vicnix.party.announce");
    }

    public static boolean isDev(ProxiedPlayer player) {
        return loadUser(player).getPrimaryGroup().equals("dev") || player.hasPermission("vicnix.party.dev");
    }

    public Configuration getConfig() {
        return this.configManager.getConfiguration();
    }
}