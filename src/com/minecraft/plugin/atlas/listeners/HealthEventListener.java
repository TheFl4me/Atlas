package com.minecraft.plugin.atlas.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealthEventListener implements Listener {

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Player) {
            if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                e.setCancelled(true);
            }
        }
    }
}
