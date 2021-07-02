package com.fabiogrossi.pluginhiderbungeecord;

import com.fabiogrossi.pluginhiderbungeecord.listeners.PlayerChatListener;
import com.fabiogrossi.pluginhiderbungeecord.listeners.PlayerTabCompleteListener;
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

public class PluginHider extends Plugin {

    private Configuration config;
    private List<String> messages;
    private List<String> messagesAdmins;
    private List<String> commands;
    private boolean sendNotification;

    public static void setLowerCase(List<String> list) {
        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
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
        if (configFile.exists()) {
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
        try (InputStream inputStream = getResourceAsStream("config.yml")) {
            if (inputStream == null) {
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

        pluginManager.registerListener(this, new PlayerChatListener(this));
        pluginManager.registerListener(this, new PlayerTabCompleteListener(this));
    }

    public BaseComponent[] colorize(String string) {
        if (string == null || string.isEmpty()) {
            log("Cannot apply color to null or empty string");
            return new BaseComponent[]{};
        }

        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', string));
    }

    public void log(String error) {
        getLogger().log(Level.SEVERE, error);
    }
}
