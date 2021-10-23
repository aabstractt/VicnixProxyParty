package me.heyimblake.proxyparty;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import lombok.Getter;
import me.heyimblake.proxyparty.commands.PartyCommand;
import me.heyimblake.proxyparty.listeners.*;
import me.heyimblake.proxyparty.mongo.MongoModel;
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

    @Getter
    private static ProxyParty instance;

    @Getter
    private LuckPerms luckPerms;
    @Getter
    private static RedisBungee redisBungee;

    private MongoModel mongo;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        this.getProxy().getPluginManager().registerCommand(this, new PartyCommand());

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

        this.mongo = new MongoModel(this.configManager.getConfiguration().getString("mongo.uri"));

        RedisProvider.getInstance().init("104.238.205.60:19847", "thatsmypassword");

        redisBungee = (RedisBungee) getProxy().getPluginManager().getPlugin("RedisBungee");
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new PlayerChatListener());
        getProxy().getPluginManager().registerListener(this, new PlayerQuitListener());
        getProxy().getPluginManager().registerListener(this, new PlayerServerSwitchListener());
        getProxy().getPluginManager().registerListener(this, new PartySendInviteListener());
        getProxy().getPluginManager().registerListener(this, new PartyCreateListener());
        getProxy().getPluginManager().registerListener(this, new PartyPlayerJoinListener());
        getProxy().getPluginManager().registerListener(this, new PartyPlayerQuitListener());
        getProxy().getPluginManager().registerListener(this, new PartyDisbandListener());
        getProxy().getPluginManager().registerListener(this, new PartyKickListener());
        getProxy().getPluginManager().registerListener(this, new ServerKickListener());
    }

    public User loadUser(String uniqueId) {
        return loadUser(UUID.fromString(uniqueId));
    }

    public User loadUser(UUID uuid) {
        try {
            return luckPerms.getUserManager().loadUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String translatePrefix(ProxiedPlayer player) {
        return translatePrefix(luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(player));
    }

    public String translatePrefix(User user) {
        String prefix = user.getCachedData().getMetaData().getPrefix();

        return (prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : ChatColor.GRAY) + user.getUsername();
    }

    public MongoModel getMongo() {
        return mongo;
    }

    public Configuration getConfig() {
        return this.configManager.getConfiguration();
    }
}