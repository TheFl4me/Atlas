package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = Atlas.getArena();
        if (arena.getAliveList().contains(player.getUniqueId())) {
            arena.eliminate(player);
            event.setDeathMessage(ChatColor.AQUA + player.getName() + " has been eliminated.");
        }
    }

    @EventHandler
    public void onBan(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.isBanned()) {
            Arena arena = Atlas.getArena();
            arena.eliminate(player);
            Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has been eliminated.");
        }
    }
}
