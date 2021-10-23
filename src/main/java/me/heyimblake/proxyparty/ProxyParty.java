package me.heyimblake.proxyparty;

import me.heyimblake.proxyparty.commands.PartyCommand;
import me.heyimblake.proxyparty.listeners.*;
import me.heyimblake.proxyparty.mongo.MongoModel;
import me.heyimblake.proxyparty.utils.ConfigManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public final class ProxyParty extends Plugin {

    private static ProxyParty instance;

    private LuckPerms luckPerms;

    private MongoModel mongo;
    private ConfigManager configManager;

    public static ProxyParty getInstance() {
        return instance;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

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

    public String translatePrefix(ProxiedPlayer player) {
        User user = luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(player);

        String prefix = user.getCachedData().getMetaData().getPrefix();

        return (prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : ChatColor.GRAY) + player.getName();
    }

    public MongoModel getMongo() {
        return mongo;
    }

    public Configuration getConfig() {
        return this.configManager.getConfiguration();
    }
}