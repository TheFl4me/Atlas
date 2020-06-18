package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.manager.Arena;
import com.minecraft.plugin.atlas.manager.Feast;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FeastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("feast")) {
            Arena arena = Atlas.getArena();
            Feast feast = new Feast();
            arena.addFeast(feast);
            feast.spawn();
            feast.spawnLoot();
            return true;
        }
        return false;
    }
}
