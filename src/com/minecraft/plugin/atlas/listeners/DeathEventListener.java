package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = Atlas.getArena();
        if (arena.getAliveList().contains(player.getUniqueId())) {
            arena.eliminate(player);
        }
    }
}
