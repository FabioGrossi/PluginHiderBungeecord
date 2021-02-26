package com._FabioTNT.pluginhiderbungeecord.listeners;

import com._FabioTNT.pluginhiderbungeecord.PluginMain;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.event.*;

public final class PlayerChatListener implements Listener {
    @EventHandler(priority = Byte.MAX_VALUE)
    public void onPlayerChat(final ChatEvent event) {
        if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer) || !event.isCommand()) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer)event.getSender();

        if (player.hasPermission("phb.bypass")) {
            return;
        }

        String command = event.getMessage().split(" ")[0].toLowerCase();

        if (command.length() < 1) {
            return;
        }

        command = command.substring(1);

        PluginMain instance = PluginMain.getInstance();

        if (!instance.getCommands().contains(command.toLowerCase()) || player.hasPermission("phb.bypass." + command)) {
            return;
        }

        event.setCancelled(true);

        for(String message : instance.getMessages()) {
            player.sendMessage(instance.colorize(message.replace("{command}", command)));
        }

        if(!instance.isSendNotification()) {
            return;
        }

        for(ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("phb.notify")) {
                for(String message : instance.getMessagesAdmins()) {
                    proxiedPlayer.sendMessage(instance.colorize(
                            message
                                    .replace("{command}", command)
                                    .replace("{player}", player.getName())
                    ));
                }
            }
        }
    }
}
