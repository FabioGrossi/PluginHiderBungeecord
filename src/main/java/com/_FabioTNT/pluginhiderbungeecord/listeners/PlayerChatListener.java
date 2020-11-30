package com._FabioTNT.pluginhiderbungeecord.listeners;

import com._FabioTNT.pluginhiderbungeecord.Main;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.event.*;

public final class PlayerChatListener implements Listener
{
    private final Main plugin;
    
    public PlayerChatListener(final Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = Byte.MAX_VALUE)
    public void onPlayerChat(final ChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        if (!event.isCommand()) {
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
        if (player.hasPermission("phb.bypass." + command)) {
            return;
        }
        if (this.plugin.equalsIgnoreCase(this.plugin.getBlockedCommands(), command)) {
            event.setCancelled(true);
            for (final String message : this.plugin.getBlockedCommandMessage()) {
                player.sendMessage(this.plugin.transformString(message.replace("{command}", command)));
            }
            for (final ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                if (online.hasPermission("phb.notify")) {
                    for (final String message2 : this.plugin.getBlockedCommandMessageAdmin()) {
                        online.sendMessage(this.plugin.transformString(message2.replace("{player}", player.getName()).replace("{command}", command)));
                    }
                }
            }
        }
    }
}
