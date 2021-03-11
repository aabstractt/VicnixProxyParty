package me.heyimblake.proxyparty;

import me.heyimblake.proxyparty.commands.PartyCommand;
import me.heyimblake.proxyparty.listeners.*;
import me.heyimblake.proxyparty.mongo.MongoModel;
import me.heyimblake.proxyparty.utils.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

public final class ProxyParty extends Plugin {

    private static ProxyParty instance;

    private MongoModel mongo;
    private ConfigManager configManager;

    public static ProxyParty getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.getProxy().getPluginManager().registerCommand(this, new PartyCommand());

        this.registerListeners();

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.configManager = new ConfigManager();

        this.configManager.initialize();

        this.mongo = new MongoModel(this.configManager.getString("mongo.uri"));
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

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public MongoModel getMongo() {
        return mongo;
    }
}