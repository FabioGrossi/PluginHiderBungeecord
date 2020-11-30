package com._FabioTNT.pluginhiderbungeecord;

import com._FabioTNT.pluginhiderbungeecord.listeners.PlayerChatListener;
import com._FabioTNT.pluginhiderbungeecord.listeners.PlayerTabCompleteListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public class Main extends Plugin {
    private File configFile;
    private Configuration config;
    private List<String> blockedCommandMessage;
    private List<String> blockedCommandMessageAdmin;
    private List<String> blockedCommands;

    @Override
    public void onLoad() {
        getLogger().info("Loading config...");
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
                throw new RuntimeException("Cold not create plugin data folder");
            }
            try (final InputStream inputStream = getResourceAsStream("config.yml")) {
                if (inputStream == null) {
                    throw new RuntimeException("Could not find config file");
                }
                Files.copy(inputStream, Paths.get(configFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException exception) {
                throw new RuntimeException("Could not create config file", exception);
            }
        }
        try {
            config = ConfigurationProvider.getProvider((Class) YamlConfiguration.class).load(configFile);
        }
        catch (IOException exception) {
            throw new RuntimeException("Could not load config file");
        }
        blockedCommandMessage = (List<String>)config.getStringList("blocked-command-message");
        blockedCommandMessageAdmin = (List<String>)config.getStringList("blocked-command-message-admin");
        blockedCommands = (List<String>)config.getStringList("blocked-commands");
    }

    @Override
    public void onEnable() {
        getLogger().info("Registering listeners...");
        getProxy().getPluginManager().registerListener(this, new PlayerChatListener(this));
        getProxy().getPluginManager().registerListener(this, new PlayerTabCompleteListener(this));
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfig() {
        return config;
    }

    public List<String> getBlockedCommandMessage() {
        return Collections.unmodifiableList((List<? extends String>)blockedCommandMessage);
    }

    public List<String> getBlockedCommandMessageAdmin() {
        return Collections.unmodifiableList((List<? extends String>)blockedCommandMessageAdmin);
    }

    public List<String> getBlockedCommands() {
        return Collections.unmodifiableList((List<? extends String>)blockedCommands);
    }

    public BaseComponent[] transformString(final String string) {
        if (string == null) {
            throw new NullPointerException("string cannot be null");
        }
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', string));
    }

    public boolean equalsIgnoreCase(final List<String> list, final String searchString) {
        if (list == null || searchString == null) {
            return false;
        }
        if (searchString.isEmpty()) {
            return true;
        }
        for (final String string : list) {
            if (string == null) {
                continue;
            }
            if (string.equalsIgnoreCase(searchString)) {
                return true;
            }
        }
        return false;
    }
}
