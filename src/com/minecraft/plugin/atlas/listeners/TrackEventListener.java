package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.manager.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class TrackEventListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.hasItem()) {
            if (event.getItem().getType() == Material.COMPASS && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                Arena arena = Atlas.getArena();
                Player p = event.getPlayer();
                Location loc = p.getLocation();
                Block under = loc.getBlock().getRelative(BlockFace.DOWN);
                if(under.getType() != Material.EMERALD_BLOCK) {
                    p.sendMessage(ChatColor.RED + "You are not standing on a tracker!");
                    return;
                }
                if(!(getRange(loc, BlockFace.NORTH) == getRange(loc, BlockFace.EAST) && getRange(loc, BlockFace.NORTH) == getRange(loc, BlockFace.SOUTH) && getRange(loc, BlockFace.NORTH) == getRange(loc, BlockFace.WEST))) {
                    p.sendMessage(ChatColor.RED + "Not all tracker sides are equal!");
                    return;
                }
                double range = getRange(loc, BlockFace.NORTH);
                Location nearest = null;
                for (UUID uuid : arena.getPlayerList().keySet()) {
                    Location potential = arena.getPlayerList().get(uuid).getLocation();
                    if (loc.distance(potential) < range) {
                        Player target = Bukkit.getPlayer(uuid);
                        if (target != null) {
                            if (target.getLocation().distance(loc) < 10) {
                                continue;
                            }
                        }
                        if(nearest == null) {
                            nearest = potential;
                            continue;
                        }
                        if(potential.distance(loc) < nearest.distance(loc)) {
                            nearest = potential;
                        }
                    }
                }
                if(nearest != null) {
                    p.setCompassTarget(nearest);
                    p.sendMessage(ChatColor.GOLD + "You have found an Emerald block!");
                } else {
                    p.sendMessage(ChatColor.RED + "No Emerald in range.");
                }
            }
        }
    }

    private double getRange(Location loc, BlockFace face) {
        Block under = loc.getBlock().getRelative(BlockFace.DOWN);
        Block next = under.getRelative(face);
        double range = 0;
        int i = 0;
        while(next.getType() == Material.OBSIDIAN) {
            i++;
            next = next.getRelative(face);
        }
        if(next.getType() == Material.GOLD_BLOCK)
            range = i * 150;
        return range;
    }
}
