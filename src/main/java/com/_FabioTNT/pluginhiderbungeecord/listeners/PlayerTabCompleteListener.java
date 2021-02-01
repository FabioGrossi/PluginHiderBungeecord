package com._FabioTNT.pluginhiderbungeecord.listeners;

import com._FabioTNT.pluginhiderbungeecord.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerTabCompleteListener implements Listener {
    private final Main plugin;

    public PlayerTabCompleteListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = 127)
    public void onPlayerTab(TabCompleteEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer player = (ProxiedPlayer)event.getSender();
        if (player.hasPermission("phb.bypass"))
            return;
        String command = event.getCursor().split(" ")[0].toLowerCase();
        if (command.length() < 1)
            return;
        command = command.substring(1);
        if (player.hasPermission("phb.bypass." + command))
            return;
        if (plugin.equalsIgnoreCase(plugin.getBlockedCommands(), command))
            event.setCancelled(true);
    }
}
