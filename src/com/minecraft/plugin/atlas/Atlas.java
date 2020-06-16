package com.minecraft.plugin.atlas;

import com.minecraft.plugin.atlas.commands.*;
import com.minecraft.plugin.atlas.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Atlas extends JavaPlugin {

    private static Atlas plugin;
    private static Arena arena;

    public static final String SPACER = ChatColor.STRIKETHROUGH + "----------------------------------";

    public void onEnable() {
        plugin = this;
        arena = new Arena();
        getArena().initialize();

        loadCommands();
        loadEvents();
    }

    public void onDisable() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.kickPlayer(ChatColor.RED + "Reload.");
        }
    }

    private void loadCommands() {
        getCommand("inventorysee").setExecutor(new InventorySeeCommand());
        getCommand("lag").setExecutor(new LagCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("status").setExecutor(new StatusCommand());
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new SpawnEventListener(), this);
        getServer().getPluginManager().registerEvents(new EmeraldEventListener(), this);
        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new LocationEventListener(), this);
        getServer().getPluginManager().registerEvents(new CombatLogEventListener(), this);
        getServer().getPluginManager().registerEvents(new HealthEventListener(), this);
    }

    public static Atlas getPlugin() {
        return plugin;
    }

    public static Arena getArena() {
        return arena;
    }
}
