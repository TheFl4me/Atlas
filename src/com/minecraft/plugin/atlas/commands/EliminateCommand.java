package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.manager.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EliminateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("eliminate")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (player.isOp()) {
                    if (args.length > 0) {
                        @SuppressWarnings("deprecation") OfflinePlayer z = Bukkit.getOfflinePlayer(args[0]);
                        if (z != null) {
                            Arena arena = Atlas.getArena();
                            arena.eliminate(z);
                            Bukkit.broadcastMessage(ChatColor.AQUA + z.getName() + " has been eliminated.");
                            if (z.isOnline()) {
                                ((Player) z).kickPlayer(ChatColor.AQUA + "You have been eliminated.");
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
        return false;
    }
}
