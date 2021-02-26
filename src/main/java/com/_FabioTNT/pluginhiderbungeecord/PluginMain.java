package com._FabioTNT.pluginhiderbungeecord;

import com._FabioTNT.pluginhiderbungeecord.listeners.PlayerChatListener;
import com._FabioTNT.pluginhiderbungeecord.listeners.PlayerTabCompleteListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

public class PluginMain extends Plugin {
    private static PluginMain instance;
    private Configuration config;
    private List<String> messages;
    private List<String> messagesAdmins;
    private List<String> commands;
    private boolean sendNotification;

    public static PluginMain getInstance() {
        return instance;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<String> getMessagesAdmins() {
        return Collections.unmodifiableList(messagesAdmins);
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public void onLoad() {
        getLogger().info("Loading config...");

        File configFile = new File(getDataFolder(), "config.yml");

        //config exists then load values
        if(configFile.exists()) {
            instance = this;

            try {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            } catch (IOException exception) {
                log("Could not load config file, this error is critical. Contact the developer");
                exception.printStackTrace();
            }

            messages = config.getStringList("blocked-command-message");
            messagesAdmins = config.getStringList("blocked-command-message-admin");
            commands = config.getStringList("blocked-commands");
            sendNotification = config.getBoolean("send-notifications");

            setLowerCase(commands);
            return;
        }

        //unable to create config or directory(s) for some reason - critical -
        if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
            log("Could not create plugin data folder, this error is critical. Contact the developer");
            return;
        }

        //try to create a new one from default internally stored
        try(InputStream inputStream = getResourceAsStream("config.yml")) {
            if(inputStream == null) {
                log("Internal config was not found, this error is critical. Contact the developer");
                return;
            }

            Files.copy(inputStream, configFile.toPath());
        } catch (IOException exception) {
            log("Could not create config file, this error is critical. Contact the developer");
            exception.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Registering listeners...");

        PluginManager pluginManager = getProxy().getPluginManager();

        pluginManager.registerListener(this, new PlayerChatListener());
        pluginManager.registerListener(this, new PlayerTabCompleteListener());
    }

    public BaseComponent[] colorize(String string) {
        if (string == null || string.isEmpty()) {
            log("Cannot apply color to null or empty string");
            return null;
        }

        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', string));
    }

    public void log(String error) {
        getLogger().log(Level.SEVERE, error);
    }

    public static void setLowerCase(List<String> list) {
        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
    }
}
