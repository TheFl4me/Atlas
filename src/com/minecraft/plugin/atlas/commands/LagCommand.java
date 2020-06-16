package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Server;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("lag")) {
            Server server = Server.get();
            commandSender.sendMessage(ChatColor.GRAY + "Server Lag: " + ChatColor.RESET + server.getLagPercentage() + "%");
            return true;
        }
        return false;
    }
}
