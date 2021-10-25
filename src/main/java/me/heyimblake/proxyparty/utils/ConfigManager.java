package me.heyimblake.proxyparty.utils;

import com.google.common.io.ByteStreams;
import me.heyimblake.proxyparty.ProxyParty;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class ConfigManager {

    private final String fileName = "config.yml";
    private Configuration configuration = null;

    public void initialize() {
        saveDefaultConfig();

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!ProxyParty.getInstance().getDataFolder().exists()) {
            ProxyParty.getInstance().getDataFolder().mkdir();
        }
        File file = getConfigFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream is = ProxyParty.getInstance().getResourceAsStream(fileName);
                     OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                    os.close();
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public File getConfigFile() {
        return new File(ProxyParty.getInstance().getDataFolder().getPath(), fileName);
    }

    public String getColorizedString(String key) {
        return ChatColor.translateAlternateColorCodes('&', configuration.getString(key));
    }
}