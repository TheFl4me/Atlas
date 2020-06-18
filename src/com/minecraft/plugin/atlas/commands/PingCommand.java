package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.utils.Server;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("ping")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (args.length == 0) {
                    sendPingInfo(p, p);
                } else {
                    Player z = Bukkit.getPlayer(args[0]);
                    if (z != null) {
                        sendPingInfo(p, z);
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void sendPingInfo(Player p, Player z) {
        Server server = Server.get();
        int ping = server.getPing(z);
        ChatColor color;

        if (ping <= 150)
            color = ChatColor.GREEN;
        else if (ping <= 300)
            color = ChatColor.GOLD;
        else
            color = ChatColor.RED;
        p.sendMessage(ChatColor.GRAY + z.getDisplayName() + "'s ping is " + color + Integer.toString(ping) + " ms.");
    }
}
