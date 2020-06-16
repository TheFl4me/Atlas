package com.minecraft.plugin.atlas;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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

        //TODO: Ask domi about end in 1.8 and wether border is needed or not.

    }

    public void addPlayer(Player player, BlockState emerald) {
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
}
