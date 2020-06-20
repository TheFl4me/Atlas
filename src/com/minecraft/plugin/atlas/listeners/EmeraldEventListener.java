package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.manager.Arena;
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
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.UUID;

public class EmeraldEventListener implements Listener {

    @EventHandler
    public void onEmeraldBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Arena arena = Atlas.getArena();
        if (block.getType() == Material.EMERALD_BLOCK && arena.getPlayerList().containsValue(block.getState())) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (!arena.getEmeraldList().get(block.getState()).equals(player.getUniqueId())) {
                UUID target = Bukkit.getOfflinePlayer(arena.getEmeraldList().get(block.getState())).getUniqueId();
                Location location = arena.getLastKnownLocations().get(target);

                player.sendMessage(ChatColor.GOLD + "Your compass is now locked onto the last known position of this player.");
                player.setCompassTarget(location);
            } else {
                player.sendMessage(ChatColor.RED + "You cant track yourself dumbass...");
            }
        }
    }

    @EventHandler
    public void cancelEntityBlockBreak(EntityChangeBlockEvent event) {
        Arena arena = Atlas.getArena();
        if (arena.getPlayerList().containsValue(event.getBlock().getState()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEmeraldPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Arena arena = Atlas.getArena();
        Player player = event.getPlayer();

        if (block.getType() == Material.EMERALD_BLOCK && arena.getPlayerList().containsKey(player.getUniqueId())) {
            if (!block.getWorld().getName().equalsIgnoreCase("world")) {
                event.setCancelled(true);
                return;
            }
            BlockState oldBlockState = arena.getPlayerList().get(player.getUniqueId());
            Block oldBlock = oldBlockState.getWorld().getBlockAt(oldBlockState.getLocation());

            oldBlock.setType(Material.AIR);
            oldBlockState.setData(oldBlockState.getData());
            oldBlockState.update();

            arena.removePlayer(player);
            arena.addPlayer(player, block.getState());

            arena.pushToDataBase();
        }
    }

    @EventHandler
    public void onEmeraldPistonExtend(BlockPistonExtendEvent event) {
        Arena arena = Atlas.getArena();
        for (Block block : event.getBlocks()) {
            if (arena.getPlayerList().containsValue(block.getState())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEmeraldPistonRetract(BlockPistonRetractEvent event) {
        Arena arena = Atlas.getArena();
        for (Block block : event.getBlocks()) {
            if (arena.getPlayerList().containsValue(block.getState())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEmeraldExplode(EntityExplodeEvent event) {
        Arena arena = Atlas.getArena();
        event.blockList().removeIf(block -> arena.getPlayerList().containsValue(block.getState()));
    }
}
