package com.minecraft.plugin.atlas;

import com.minecraft.plugin.atlas.commands.*;
import com.minecraft.plugin.atlas.database.Database;
import com.minecraft.plugin.atlas.database.DatabaseCore;
import com.minecraft.plugin.atlas.database.MySQLCore;
import com.minecraft.plugin.atlas.listeners.*;
import com.minecraft.plugin.atlas.manager.Arena;
import com.minecraft.plugin.atlas.utils.Server;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Atlas extends JavaPlugin {

    public static final String DIRECTORY_ATLAS = "plugins" + File.separator + "Atlas";
    public static final String DIRECTORY_DATABASE = DIRECTORY_ATLAS + File.separator + "Database.yml";

    public static final String DB_PLAYERS = "players";

    private static Atlas plugin;
    private static Arena arena;
    private static Database db;

    public static final String SPACER = ChatColor.STRIKETHROUGH + "----------------------------------";

    public void onEnable() {
        plugin = this;

        loadFiles();
        loadDatabase();
        loadCommands();
        loadEvents();

        arena = new Arena();
        getArena().initialize();
        getArena().pullFromDataBase();

        Server server = new Server();
        server.initiate();
    }

    public void onDisable() {
        arena.pushToDataBase();

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.kickPlayer(ChatColor.RED + "Reload.");
        }


    }

    private void loadCommands() {
        getCommand("invsee").setExecutor(new InventorySeeCommand());
        getCommand("lag").setExecutor(new LagCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("status").setExecutor(new StatusCommand());
        getCommand("reset").setExecutor(new ResetCommand());
        getCommand("feast").setExecutor(new FeastCommand());
        getCommand("eliminate").setExecutor(new EliminateCommand());
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new SpawnEventListener(), this);
        getServer().getPluginManager().registerEvents(new EmeraldEventListener(), this);
        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new LocationEventListener(), this);
        getServer().getPluginManager().registerEvents(new CombatLogEventListener(), this);
        getServer().getPluginManager().registerEvents(new HealthEventListener(), this);
    }

    private void loadDatabase() {
        File database = new File(DIRECTORY_DATABASE);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(database);

        String prefix = "[Atlas - Database]";
        String host = cfg.getString("host");
        String user = cfg.getString("user");
        String pass = cfg.getString("pass");
        String name = cfg.getString("name");
        String port = cfg.getString("port");

        DatabaseCore core = new MySQLCore(host, user, pass, name, port);

        System.out.println(prefix + " Connecting...");
        try {
            db = new Database(core);
        } catch (Exception e) {
            System.out.println(prefix + " Connection failed! Shutting down server...");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return;
        }
        System.out.println(prefix + " Connected!");
        System.out.println(prefix + " Setting up...");
        try {
            if (!db.hasTable(DB_PLAYERS)) {
                String query = "CREATE TABLE " + DB_PLAYERS + " ("
                        + " uuid TEXT(50) NOT NULL,"
                        + " alive BOOLEAN NOT NULL,"
                        + " blockX INT NOT NULL DEFAULT 0,"
                        + " blockY INT NOT NULL DEFAULT 0,"
                        + " blockZ INT NOT NULL DEFAULT 0,"
                        + " blockWorld TEXT(50) NOT NULL,"
                        + " locX INT NOT NULL DEFAULT 0,"
                        + " locY INT NOT NULL DEFAULT 0,"
                        + " locZ INT NOT NULL DEFAULT 0,"
                        + " locWorld TEXT(50) NOT NULL);";
                db.createTable(query, DB_PLAYERS);
            }
            System.out.println(prefix + " Set up done!");
        } catch (SQLException e) {
            System.out.println(prefix + " Setup failed!");
            e.printStackTrace();
        }
    }

    private void loadFiles() {
        File general = new File(DIRECTORY_ATLAS);
        File database = new File(DIRECTORY_DATABASE);
        if (!general.exists())
            general.mkdir();
        if (!database.exists()) {
            try {
                database.createNewFile();
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(database);
                cfg.set("host", "host");
                cfg.set("user", "user");
                cfg.set("pass", "password");
                cfg.set("name", "name");
                cfg.set("port", "port");
                cfg.save(database);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Atlas getPlugin() {
        return plugin;
    }

    public static Arena getArena() {
        return arena;
    }

    public static Database getDataBase() {
        return db;
    }
}
