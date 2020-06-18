package com.minecraft.plugin.atlas.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventorySeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("invsee")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (player.isOp()) {
                    if (args.length > 0) {
                        Player z = Bukkit.getPlayer(args[0]);
                        if (z != null) {
                            Inventory inv = z.getPlayer().getInventory();
                            player.openInventory(inv);
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
