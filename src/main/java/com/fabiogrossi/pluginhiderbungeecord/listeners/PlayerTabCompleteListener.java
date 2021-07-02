package com.fabiogrossi.pluginhiderbungeecord.listeners;

import com.fabiogrossi.pluginhiderbungeecord.PluginHider;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerTabCompleteListener implements Listener {

    private final PluginHider pluginHider;

    public PlayerTabCompleteListener(PluginHider pluginHider) {
        this.pluginHider = pluginHider;
    }

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

        if (pluginHider.getCommands().contains(command.toLowerCase()) && !player.hasPermission("phb.bypass." + command)) {
            event.setCancelled(true);
        }
    }
}
