package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.database.Database;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Random;
import java.util.UUID;

public class SpawnEventListener implements Listener {

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Arena arena = Atlas.getArena();
        Player player = event.getPlayer();
        Database db = Atlas.getDataBase();

        if (!db.containsValue(Atlas.DB_PLAYERS, "uuid", player.getUniqueId().toString())) {
            db.execute("INSERT INTO " + Atlas.DB_PLAYERS + " (" +
                    "uuid" +
                    ", alive" +
                    ", blockX" +
                    ", blockY" +
                    ", blockZ" +
                    ", blockWorld" +
                    ", locX" +
                    ", locY" +
                    ", locZ" +
                    ", locWorld" +
                    ") VALUES (" +
                    "'" + player.getUniqueId().toString() +"', " +
                    "" + true + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + "'world'" + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + "'world'" + "" +
                    ");");
        }


        if (!arena.getLastKnownLocations().containsKey(player.getUniqueId())) {
            arena.spawn(player);
        }

        arena.updateScoreboard();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Arena arena = Atlas.getArena();
        Player player = event.getPlayer();
        if (!arena.getAliveList().contains(player.getUniqueId()) && !player.isOp() && arena.getLastKnownLocations().containsKey(player.getUniqueId())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.AQUA + "You have been eliminated.");
        }
    }
}
