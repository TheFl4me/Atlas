package com.minecraft.plugin.atlas;

import com.minecraft.plugin.atlas.database.Database;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.minecraft.plugin.atlas.Atlas.DB_PLAYERS;

public class Arena {

    private Map<UUID, BlockState> players;
    private Map<UUID, Location> locations;
    private List<UUID> alive;
    private Map<UUID, BukkitRunnable> combatLogTask;
    private int size;

    public Arena() {
        this.players = new HashMap<>();
        this.locations = new HashMap<>();
        this.alive = new ArrayList<>();
        this.combatLogTask = new HashMap<>();
        this.size = 100;
    }

    public List<UUID> getAliveList() {
        return this.alive;
    }

    public void addAlive(Player player) {
        this.alive.add(player.getUniqueId());
    }

    public void addAlive(OfflinePlayer player) {
        this.alive.add(player.getUniqueId());
    }

    public void removeAlive(Player player) {
        this.alive.remove(player.getUniqueId());
    }

    public Map<UUID, BlockState> getPlayerList() {
        return this.players;
    }

    public Map<UUID, Location> getLastKnownLocations() {
        return this.locations;
    }

    public Map<BlockState, UUID> getEmeraldList() {
        Map<BlockState, UUID> blocks = new HashMap<>();
        for (UUID key : this.getPlayerList().keySet()) {
            blocks.put(this.getPlayerList().get(key), key);
        }
        return blocks;
    }

    public int getSize() {
        return this.size;
    }

    public void initialize() {
        World world = Bukkit.getWorld("world");
        WorldBorder worldBorder = world.getWorldBorder();

        world.setSpawnLocation(0 , 64,0);
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(this.getSize());

        World nether = Bukkit.getWorld("world_nether");
        WorldBorder netherBorder = nether.getWorldBorder();

        netherBorder.setCenter(0,0);
        netherBorder.setSize(this.getSize() / 8);

        World end = Bukkit.getWorld("world_end");
        WorldBorder endBorder = end.getWorldBorder();

        endBorder.setCenter(0,0);
        endBorder.setSize(400);

    }

    public void addPlayer(Player player, BlockState emerald) {
        this.players.put(player.getUniqueId(), emerald);
    }

    public void addPlayer(OfflinePlayer player, BlockState emerald) {
        this.players.put(player.getUniqueId(), emerald);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public void eliminate(Player player) {
        BlockState blockState = this.getPlayerList().get(player.getUniqueId());
        Block block = Bukkit.getWorld("world").getBlockAt(blockState.getLocation());

        block.setType(Material.AIR);
        blockState.setData(blockState.getData());
        blockState.update();

        this.removePlayer(player);
        this.removeAlive(player);
        this.removeLocation(player);

        player.setGameMode(GameMode.SPECTATOR);
    }

    public void addLocation(Player player, Location location) {
        this.locations.put(player.getUniqueId(), location);
    }

    public void addLocation(OfflinePlayer player, Location location) {
        this.locations.put(player.getUniqueId(), location);
    }

    public void removeLocation(Player player) {
        this.locations.remove(player.getUniqueId());
    }

    public boolean isCombatLog(Player player) {
        return combatLogTask.containsKey(player.getUniqueId());
    }

    public BukkitRunnable getCombatLogTask(Player player) {
        return this.combatLogTask.get(player.getUniqueId());
    }

    public void setCombatLog(Player player) {
        if(this.isCombatLog(player)) {
            this.cancelCombatLog(player);
        }
        this.combatLogTask.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(player.getUniqueId());
                if(p != null)
                    if (isCombatLog(p)) {
                        p.sendMessage(ChatColor.GREEN + "You can now safely disconnect again.");
                        cancelCombatLog(p);
                    }
                    else
                        cancel();
            }
        });
        this.getCombatLogTask(player).runTaskLater(Atlas.getPlugin(), 200);
    }

    public void cancelCombatLog(Player player) {
        this.getCombatLogTask(player).cancel();
        this.combatLogTask.remove(player.getUniqueId());
    }

    public void pushToDataBase() {
        Database db = Atlas.getDataBase();
        for (UUID uuid : this.getPlayerList().keySet()) {

            Location blockLoc = this.getPlayerList().get(uuid).getLocation();
            Location lasLoc = this.getLastKnownLocations().get(uuid);

            db.execute("UPDATE " + Atlas.DB_PLAYERS + " SET " +
                    "blockX = " + blockLoc.getBlockX() + ", " +
                    "blockY = " + blockLoc.getBlockY() + ", " +
                    "blockZ = " + blockLoc.getBlockZ() + ", " +
                    "blockWorld = " + blockLoc.getWorld().getName() + ", " +
                    "locX = " + lasLoc.getBlockX() + ", " +
                    "locY = " + lasLoc.getBlockY() + ", " +
                    "locZ = " + lasLoc.getBlockZ() + " " +
                    "locWorld = " + lasLoc.getWorld().getName() + ", " +
                    "WHERE UUID = " + uuid.toString() + ");");
        }
    }

    public void pullFromDataBase() {
        Database db = Atlas.getDataBase();
        try {
            ResultSet sets = db.select(DB_PLAYERS);
            while (sets.next()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(sets.getString("UUID")));
                boolean alive = sets.getBoolean("alive");
                int blockX = sets.getInt("blockX");
                int blockY = sets.getInt("blockY");
                int blockZ = sets.getInt("blockZ");
                World blockWorld = Bukkit.getWorld(sets.getString("blockWorld"));
                int locX = sets.getInt("locX");
                int locY = sets.getInt("locY");
                int locZ = sets.getInt("locZ");
                World locWorld = Bukkit.getWorld(sets.getString("locWorld"));

                Block block = Bukkit.getWorld(blockWorld.getName()).getBlockAt(blockX, blockY, blockZ);
                Location lastLoc = new Location(locWorld, locX, locY, locZ);

                this.addPlayer(player, block.getState());
                this.addLocation(player, lastLoc);
                if (alive)
                    this.addAlive(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
