package com._FabioTNT.pluginhiderbungeecord.listeners;

import com._FabioTNT.pluginhiderbungeecord.PluginMain;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerTabCompleteListener implements Listener {
    @EventHandler(priority = 127)
    public void onPlayerTab(TabCompleteEvent event) {
        if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (player.hasPermission("phb.bypass")) {
            return;
        }

        String command = event.getCursor().split(" ")[0].toLowerCase();

        if (command.length() < 1) {
            return;
        }

        command = command.substring(1);
        PluginMain instance = PluginMain.getInstance();

        if(instance.getCommands().contains(command.toLowerCase()) && !player.hasPermission("phb.bypass." + command)) {
            event.setCancelled(true);
        }
    }
}
