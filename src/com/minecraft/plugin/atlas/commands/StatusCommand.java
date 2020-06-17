package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.utils.Server;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("status") && commandSender instanceof Player) {
            ChatColor mainColor = ChatColor.GRAY;
            ChatColor secColor = ChatColor.WHITE;
            Player p = (Player) commandSender;
            World world = p.getWorld();
            long maxMemory = Runtime.getRuntime().maxMemory();
            Server server = Server.get();
            String status = mainColor + Atlas.SPACER + "\n" +
                    "" + mainColor + "Online players: " + secColor + Bukkit.getOnlinePlayers().size() + "\n" +
                    "" + mainColor + "Pending tasks: " + secColor + Bukkit.getScheduler().getPendingTasks().size() + "\n" +
                    "" + mainColor + "TPS: " + secColor + server.getTPS() + "\n" +
                    "" + mainColor + "Entities (World = " + world.getName() + ": " + secColor + world.getEntities().size() + "\n" +
                    "" + mainColor + "Mobs (World = " + world.getName() + ": " + secColor + world.getEntitiesByClass(LivingEntity.class).size() + "\n" +
                    "" + mainColor + "Free memory: " + secColor + Runtime.getRuntime().freeMemory() / 1048576 + "MB" + "\n" +
                    "" + mainColor + "Used memory: " + secColor + ((Runtime.getRuntime().totalMemory() / 1048576) - (Runtime.getRuntime().freeMemory() / 1048576)) + "MB" + "\n" +
                    "" + mainColor + "Maximum memory: " + secColor + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / 1048576 + "MB") + "\n" +
                    "" + mainColor + "Total memory: " + secColor + Runtime.getRuntime().totalMemory() / 1048576 + "MB\n" +
                    "" + mainColor + Atlas.SPACER;
            p.sendMessage(status);
            return true;
        }
        return false;
    }
}
