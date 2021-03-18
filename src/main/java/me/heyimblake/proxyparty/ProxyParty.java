package me.heyimblake.proxyparty;

import me.heyimblake.proxyparty.commands.PartyCommand;
import me.heyimblake.proxyparty.listeners.*;
import me.heyimblake.proxyparty.mongo.MongoModel;
import me.heyimblake.proxyparty.partyutils.PartyPermission;
import me.heyimblake.proxyparty.utils.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public final class ProxyParty extends Plugin {

    private static ProxyParty instance;

    private MongoModel mongo;
    private ConfigManager configManager;

    private final Map<String, PartyPermission> partyPermissionMap = new HashMap<>();

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

        this.initPartyPermissions();

        this.mongo = new MongoModel(this.configManager.getString("mongo.uri"));
    }

    private void initPartyPermissions() {
        Configuration config = (Configuration) configManager.getConfiguration().get("permissions-party-size");

        for (String permission : config.getKeys()) {
            Configuration configuration = (Configuration) config.get(permission);

            this.partyPermissionMap.put(permission, new PartyPermission(permission, configuration.getString("prefix"), configuration.getInt("size")));
        }
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

    public PartyPermission getPartyPermissions(ProxiedPlayer player) {
        PartyPermission betterPartyPermissions = this.partyPermissionMap.get("default-party");

        for (PartyPermission partyPermission : this.partyPermissionMap.values()) {
            if (betterPartyPermissions == null) {
                betterPartyPermissions = partyPermission;
            }

            if (!player.hasPermission(partyPermission.getName())) continue;

            if (partyPermission.getSize() >= betterPartyPermissions.getSize()) {
                betterPartyPermissions = partyPermission;
            }
        }

        return betterPartyPermissions;
    }

    public String translatePrefix(ProxiedPlayer player) {
        PartyPermission partyPermission = this.getPartyPermissions(player);

        return ChatColor.translateAlternateColorCodes('&', partyPermission.getPrefix().replace("{name}", player.getName()));
    }

    public MongoModel getMongo() {
        return mongo;
    }
}