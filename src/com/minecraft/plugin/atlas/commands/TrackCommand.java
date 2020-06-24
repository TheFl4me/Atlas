package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.manager.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TrackCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("track")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (p.isOp() && p.getGameMode() == GameMode.SPECTATOR) {
                    if (args.length > 0) {
                        Arena arena = Atlas.getArena();
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                        Location potential = arena.getLastKnownLocations().get(target.getUniqueId());
                        if (potential != null) {
                            p.sendMessage(ChatColor.GOLD + target.getName() + " - X:" + potential.getBlockX() + ", Y: " + potential.getBlockY() + ", Z: " + potential.getBlockZ() + " (" + Double.toString(Math.round(p.getLocation().distance(potential))) + " blocks)");
                        }
                        BlockState block = arena.getPlayerList().get(target.getUniqueId());
                        if (block != null) {
                            Location blockLoc = block.getLocation();
                            p.sendMessage(ChatColor.GREEN + "Emerald - X: " + blockLoc.getBlockX() + ", Y: " + blockLoc.getBlockY() + ", Z: " + blockLoc.getBlockZ() + " (" + Double.toString(Math.round(p.getLocation().distance(blockLoc))) + " blocks)");
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }
}
