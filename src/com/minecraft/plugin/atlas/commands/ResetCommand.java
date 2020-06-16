package com.minecraft.plugin.atlas.commands;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("reset") && commandSender.isOp()) {
            Arena arena = Atlas.getArena();
            arena.reset();
            return true;
        }
        return false;
    }
}
