package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.Bukkit;
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
            if (event.getPlayer() instanceof Player) {
                UUID target = Bukkit.getOfflinePlayer(arena.getEmeraldList().get(block.getState())).getUniqueId();
                Player player = (Player) event.getPlayer();
                Location location = arena.getLastKnownLocations().get(target);

                player.sendMessage("The players last known location to whom this block belongs is:\n" +
                        "World: " + location.getWorld().getName() + "\n" +
                        "X: " + Integer.toString(location.getBlockX()) + "\n" +
                        "Z: " + Integer.toString(location.getBlockZ()) + "\n" +
                        "Y: " + Integer.toString(location.getBlockY()));
            }
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
        }
    }
}
