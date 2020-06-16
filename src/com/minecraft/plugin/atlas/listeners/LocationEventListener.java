package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LocationEventListener implements Listener {

    @EventHandler
    public void onMoveSaveLocation(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Arena arena = Atlas.getArena();
        if (arena.getAliveList().contains(player.getUniqueId())) {
            arena.removeLocation(player);
            arena.addLocation(player, player.getLocation());
        }
    }
}
