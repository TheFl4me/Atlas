package com.minecraft.plugin.atlas.listeners;

import com.minecraft.plugin.atlas.Arena;
import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class SpawnEventListener implements Listener {

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Arena arena = Atlas.getArena();
        Player player = event.getPlayer();
        Database db = Atlas.getDataBase();

        if (!db.containsValue(Atlas.DB_PLAYERS, "UUID", player.getUniqueId().toString())) {
            db.execute("INSERT INTO " + Atlas.DB_PLAYERS + "(" +
                    "UUID" +
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
                    "" + player.getUniqueId().toString() +", " +
                    "" + true + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + "world" + ", " +
                    "" + 0 + ", " +
                    "" + 0 + ", " +
                    "" + 0 + "" +
                    "" + "world" + "" +
                    ");");
        }


        if (!arena.getPlayerList().containsKey(player.getUniqueId())) {

            Random r1 = new Random();
            Random r2 = new Random();
            int rX = r1.nextInt(arena.getSize()) - arena.getSize() / 2;
            int rZ = r2.nextInt(arena.getSize()) - arena.getSize() / 2;

            Location emeraldLoc = Bukkit.getWorld("world").getHighestBlockAt(rX, rZ).getLocation();
            Location spawn = emeraldLoc.clone();
            spawn.setY(spawn.getY() + 1);

            player.teleport(spawn);

            Block emerald = Bukkit.getWorld("world").getBlockAt(emeraldLoc);
            emerald.setType(Material.EMERALD_BLOCK);

            arena.addPlayer(player, emerald.getState());
            arena.addAlive(player);
            arena.addLocation(player, player.getLocation());
        }

        if (!arena.getAliveList().contains(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
}
