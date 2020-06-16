package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.UUID;

public class EmeraldEventListener implements Listener {

    @EventHandler
    public void onEmeraldBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Arena arena = Atlas.getArena();
        if (block.getType() == Material.EMERALD_BLOCK && arena.getPlayerList().containsValue(block.getState())) {
            event.setCancelled(true);
            UUID target = Bukkit.getOfflinePlayer(arena.getEmeraldList().get(block.getState())).getUniqueId();
            Player player = event.getPlayer();
            Location location = arena.getLastKnownLocations().get(target);

            player.sendMessage(ChatColor.RED + "Your compass is now locked onto the last known position in the \"" + location.getWorld().getName() + "\" world of this player.");
            player.setCompassTarget(location);
        }
    }

    @EventHandler
    public void cancelEntityBlockBreak(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        Arena arena = Atlas.getArena();
        if (block.getType() == Material.EMERALD_BLOCK && arena.getPlayerList().containsValue(block.getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmeraldPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Arena arena = Atlas.getArena();
        Player player = event.getPlayer();

        if (block.getType() == Material.EMERALD_BLOCK && arena.getPlayerList().containsKey(player.getUniqueId())) {
            BlockState oldBlockState = arena.getPlayerList().get(player.getUniqueId());
            Block oldBlock = Bukkit.getWorld("world").getBlockAt(oldBlockState.getLocation());

            oldBlock.setType(Material.AIR);
            oldBlockState.setData(oldBlockState.getData());
            oldBlockState.update();

            arena.removePlayer(player);
            arena.addPlayer(player, block.getState());

            arena.pushToDataBase();
        }
    }
}
