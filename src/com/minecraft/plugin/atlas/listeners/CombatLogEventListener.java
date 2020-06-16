package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatLogEventListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Arena arena = Atlas.getArena();
        Player p = e.getPlayer();
        if(arena.isCombatLog(p)) {
            arena.cancelCombatLog(p);
            p.setHealth(0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerFightCheckCombatLog(EntityDamageByEntityEvent e) {
        if (e.isCancelled() || e.getDamage() == 0)
            return;
        if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Player)) {
            Player hitter = (Player) e.getDamager();
            Player target = (Player) e.getEntity();
            if (!hitter.getGameMode().equals(GameMode.SPECTATOR) && !target.getGameMode().equals(GameMode.SPECTATOR)) {
                Arena arena = Atlas.getArena();
                arena.setCombatLog(hitter);
                arena.setCombatLog(target);
            }
        }
    }
}
