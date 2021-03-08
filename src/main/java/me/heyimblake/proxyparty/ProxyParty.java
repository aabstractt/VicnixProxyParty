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

        getProxy().getPluginManager().registerCommand(this, new PartyCommand());

        registerListeners();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        configManager = new ConfigManager();

        configManager.initialize();

        this.mongo = new MongoModel(configManager.getString("mongo.uri"));
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
        getProxy().getPluginManager().registerListener(this, new PartyDenyInviteListener());
        getProxy().getPluginManager().registerListener(this, new PartyWarpListener());
        getProxy().getPluginManager().registerListener(this, new PartyRetractInviteListener());
        getProxy().getPluginManager().registerListener(this, new PartyAcceptInviteListener());
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MongoModel getMongo() {
        return mongo;
    }
}
