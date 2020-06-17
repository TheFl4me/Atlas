package com.minecraft.plugin.atlas;

import com.minecraft.plugin.atlas.database.Database;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Arena {

    private Map<UUID, BlockState> players;
    private Map<UUID, Location> locations;
    private List<UUID> alive;
    private Map<UUID, BukkitRunnable> combatLogTask;
    private int size;
    private boolean ingame;

    public Arena() {
        this.players = new HashMap<>();
        this.locations = new HashMap<>();
        this.alive = new ArrayList<>();
        this.combatLogTask = new HashMap<>();
        this.size = 200;
        this.ingame = true;
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
        world.setDifficulty(Difficulty.HARD);

        World nether = Bukkit.getWorld("world_nether");
        WorldBorder netherBorder = nether.getWorldBorder();
        nether.setDifficulty(Difficulty.HARD);

        netherBorder.setCenter(0,0);
        netherBorder.setSize(this.getSize() / 8);

        World end = Bukkit.getWorld("world_the_end");
        WorldBorder endBorder = end.getWorldBorder();
        end.setDifficulty(Difficulty.HARD);

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
        Block block = blockState.getWorld().getBlockAt(blockState.getLocation());

        block.setType(Material.AIR);
        blockState.setData(blockState.getData());
        blockState.update();

        this.removePlayer(player);
        this.removeAlive(player);

        this.updateScoreboard();
        player.setGameMode(GameMode.SPECTATOR);

        //Check for winner
        if (this.getAliveList().size() == 1) {
            OfflinePlayer winner = Bukkit.getOfflinePlayer(this.getAliveList().get(0));
            this.win(winner);
        }
    }

    public void win(OfflinePlayer winner) {
        this.setInGame(false);
        if (winner.isOnline()) {
            Player player = (Player) winner;
            if(this.isCombatLog(player)) {
                this.cancelCombatLog(player);
                player.sendMessage(ChatColor.GREEN + "You can now safely disconnect again.");
            }
        }
        Bukkit.getScheduler().runTaskTimer(Atlas.getPlugin(), () -> {
            if (winner.isOnline()) {
                Player player = (Player) winner;
                player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            }
            for(Player players : Bukkit.getOnlinePlayers()) {
                players.sendMessage(ChatColor.GOLD + winner.getName() + " has won the tournament!!");
            }
        }, 40, 40);
        Bukkit.getScheduler().runTaskLater(Atlas.getPlugin(), this::reset, 600);
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
                        player.sendMessage(ChatColor.GREEN + "You can now safely disconnect again.");
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

    public boolean isInGame() {
        return this.ingame;
    }

    public void setInGame(boolean bool) {
        this.ingame = bool;
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
                    "blockWorld = '" + blockLoc.getWorld().getName() + "', " +
                    "locX = " + lasLoc.getBlockX() + ", " +
                    "locY = " + lasLoc.getBlockY() + ", " +
                    "locZ = " + lasLoc.getBlockZ() + ", " +
                    "locWorld = '" + lasLoc.getWorld().getName() + "' " +
                    "WHERE uuid = '" + uuid.toString() + "';");
        }
    }

    public void pullFromDataBase() {
        Database db = Atlas.getDataBase();
        try {
            ResultSet sets = db.select(Atlas.DB_PLAYERS);
            while (sets.next()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(sets.getString("uuid")));
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

    public void reset() {

        for (UUID uuid : this.getPlayerList().keySet()) {
            BlockState oldBlockState = this.getPlayerList().get(uuid);
            Block oldBlock = oldBlockState.getWorld().getBlockAt(oldBlockState.getLocation());

            oldBlock.setType(Material.AIR);
            oldBlockState.setData(oldBlockState.getData());
            oldBlockState.update();
        }

        this.players.clear();
        this.combatLogTask.clear();
        this.locations.clear();
        this.alive.clear();

        Database db = Atlas.getDataBase();
        db.execute("DROP TABLE " + Atlas.DB_PLAYERS);

        Bukkit.getServer().reload();
    }

    public void updateScoreboard() {
        for(Player players : Bukkit.getOnlinePlayers()) {
            ChatColor color = ChatColor.GRAY;
            Scoreboard board = players.getScoreboard();
            board.getObjectives().forEach(Objective::unregister);

            Objective obj = board.registerNewObjective("test", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.setDisplayName(ChatColor.GOLD + "Alive:");

            int i = 0;
            for(UUID uuid : this.getAliveList()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                Score name = obj.getScore(color + player.getName());
                name.setScore(i);
                i++;
            }
        }
    }

    public void spawn(Player player) {
        Random r1 = new Random();
        Random r2 = new Random();
        int rX = r1.nextInt(this.getSize()) - this.getSize() / 2;
        int rZ = r2.nextInt(this.getSize()) - this.getSize() / 2;

        World world = Bukkit.getWorld("world");

        Location emeraldLoc = world.getHighestBlockAt(rX, rZ).getLocation();
        Location spawn = emeraldLoc.clone();
        spawn.setY(spawn.getY() + 1);

        Block feet = spawn.getBlock();
        Block head = feet.getRelative(BlockFace.UP);

        for (BlockFace face : BlockFace.values()) {
            feet.getRelative(face).setType(Material.AIR);
            head.getRelative(face).setType(Material.AIR);
        }

        Block emerald = emeraldLoc.getWorld().getBlockAt(emeraldLoc);
        emerald.setType(Material.EMERALD_BLOCK);

        player.teleport(spawn);

        this.addPlayer(player, emerald.getState());
        this.addAlive(player);
        this.addLocation(player, player.getLocation());
    }
}
