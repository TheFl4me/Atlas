package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.manager.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = Atlas.getArena();
        if(arena.isCombatLog(player)) {
            arena.cancelCombatLog(player);
        }
        if (arena.getAliveList().contains(player.getUniqueId())) {
            arena.eliminate(player);
            event.setDeathMessage(ChatColor.AQUA + player.getName() + " has been eliminated.");
            player.kickPlayer(ChatColor.AQUA + "You have been eliminated.");
        }
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            if(arena.isCombatLog(killer)) {
                arena.cancelCombatLog(killer);
                killer.sendMessage(ChatColor.GREEN + "You can now safely disconnect again.");
            }

            List<BlockState> list = new ArrayList<>(arena.getPlayerList().values());
            BlockState state = null;
            while (state == null) {
                Random r = new Random();
                BlockState temp = list.get(r.nextInt(list.size()));
                if (!arena.isInGame()) {
                    state = temp;
                    break;
                }
                if (!temp.equals(arena.getPlayerList().get(killer.getUniqueId()))) {
                    state = temp;
                }
            }
            killer.setCompassTarget(state.getLocation());
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

    @EventHandler
    public void disableAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
