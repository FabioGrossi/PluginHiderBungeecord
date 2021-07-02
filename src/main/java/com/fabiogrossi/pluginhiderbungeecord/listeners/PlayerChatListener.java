package com.fabiogrossi.pluginhiderbungeecord.listeners;

import com.fabiogrossi.pluginhiderbungeecord.PluginHider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerChatListener implements Listener {

    private final PluginHider pluginHider;

    public PlayerChatListener(PluginHider pluginHider) {
        this.pluginHider = pluginHider;
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void onPlayerChat(final ChatEvent event) {
        if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer) || !event.isCommand()) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (player.hasPermission("phb.bypass")) {
            return;
        }

        String command = event.getMessage().split(" ")[0].toLowerCase();

        if (command.length() < 1) {
            return;
        }

        command = command.substring(1);

        if (!pluginHider.getCommands().contains(command.toLowerCase()) || player.hasPermission("phb.bypass." + command)) {
            return;
        }

        event.setCancelled(true);

        for (String message : pluginHider.getMessages()) {
            player.sendMessage(pluginHider.colorize(message.replace("{command}", command)));
        }

        if (!pluginHider.isSendNotification()) {
            return;
        }

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("phb.notify")) {
                for (String message : pluginHider.getMessagesAdmins()) {
                    proxiedPlayer.sendMessage(pluginHider.colorize(
                            message
                                    .replace("{command}", command)
                                    .replace("{player}", player.getName())
                    ));
                }
            }
        }
    }
}
