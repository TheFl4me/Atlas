package com.minecraft.plugin.atlas.manager;

import com.minecraft.plugin.atlas.Atlas;
import com.minecraft.plugin.atlas.utils.Server;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Feast {

    private List<Location> grass;
    private List<Location> air;
    private List<Location> chests;
    private Location table;
    private long spawnTime;
    private World world;
    private BukkitRunnable task;
    private int timer;


    public Feast() {
        this.spawnTime = System.currentTimeMillis();

        Arena arena = Atlas.getArena();

        Random r4 = new Random();
        List<World> worlds = Arrays.asList(Bukkit.getWorld("world"), Bukkit.getWorld("world"), Bukkit.getWorld("world"), Bukkit.getWorld("world_nether"));
        this.world = worlds.get(r4.nextInt(worlds.size()));
        int radius = 20;
        int range = arena.getSize() / (this.getWorld().getName().equalsIgnoreCase("world") ? 1 : 8) - radius;

        Random r1 = new Random();
        Random r2 = new Random();
        Random r3 = new Random();

        int rX = r1.nextInt(range) - range / 2;
        int rZ = r2.nextInt(range) - range / 2;
        int rY = r3.nextInt(90);

        Location center = new Location(world, rX, rY + 6, rZ);
        this.grass = this.allocateCylinder(center, radius, 1);

        center.setY(center.getY() + 1);
        this.air = this.allocateCylinder(center, radius, 25);

        this.table = center;

        Block tableBlock = center.getBlock();

        this.chests = new ArrayList<>();

        this.chests.add(tableBlock.getRelative(BlockFace.NORTH_EAST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.NORTH_WEST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.SOUTH_EAST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.SOUTH_WEST).getLocation());

        this.chests.add(tableBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getLocation());
        this.chests.add( tableBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getLocation());

        this.chests.add(tableBlock.getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.NORTH_EAST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.NORTH_WEST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH_EAST).getLocation());
        this.chests.add(tableBlock.getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.SOUTH_WEST).getLocation());

        final List<Integer> updates = Arrays.asList(0,1,2,3,4,5,10,30,60, 300, 600, 1800, 3600);
        Server server = Server.get();
        spawn();
        this.timer = 3600;
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (updates.contains(getTimer())) {
                    Bukkit.broadcastMessage(ChatColor.RED + "The Feast will spawn in " + server.getTime(getTimer() * 1000) + "\n" +
                            "X: " + getTable().getBlockX() + ", Z: " + getTable().getBlockZ() + ", World: " + (getWorld().getName().equalsIgnoreCase("world") ? "Overworld" : "Nether"));
                }
                setTimer(getTimer() - 1);
                if (getTimer() == 0) {
                    getTask().cancel();
                    spawnLoot();
                }

            }
        };
        this.task.runTaskTimer(Atlas.getPlugin(), 0, 20);
    }

    public BukkitRunnable getTask() {
        return this.task;
    }

    public int getTimer() {
        return this.timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public List<Location> getGrass() {
        return this.grass;
    }

    public List<Location> getAir() {
        return this.air;
    }

    public List<Location> getChests() {
        return this.chests;
    }

    public Location getTable() {
        return this.table;
    }

    public long getSpawnTime() {
        return this.spawnTime;
    }

    public World getWorld() {
        return this.world;
    }

    private List<Location> allocateCylinder(Location loc, int r, int height) {
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        World w = loc.getWorld();
        int rSquared = r * r;

        List<Location> tempList = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                for (int z = cz - r; z <= cz + r; z++) {
                    if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                        tempList.add(new Location(w, x, cy + y, z));
                    }
                }
            }
        }
        return tempList;
    }

    public void spawn() {
        Arena arena = Atlas.getArena();

        for (Location loc : this.getGrass()) {
            Block block = loc.getBlock();
            if (!arena.getPlayerList().containsValue(block.getState())) {
                block.setType(Material.GRASS);
            }
        }

        for (Location loc : this.getAir()) {
            Block block = loc.getBlock();
            if (!arena.getPlayerList().containsValue(block.getState())) {
                block.setType(Material.AIR);
            }
        }

        Server server = Server.get();
        Bukkit.broadcastMessage(ChatColor.RED + "A Feast platform has been generated at X: " + this.getTable().getBlockX() + ", Z: " + this.getTable().getBlockZ() + ", World: " + (this.getWorld().getName().equalsIgnoreCase("world") ? "Overworld" : "Nether"));
    }

    public void spawnLoot() {
        Arena arena = Atlas.getArena();

        for (Location loc : this.getChests()) {
            Block block = loc.getBlock();
            if (!arena.getPlayerList().containsValue(block.getState())) {
                block.setType(Material.CHEST);

                if (block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();

                    Random r = new Random();
                    for(int i = 0; i < 3 + r.nextInt(15); i++)
                        ChestItem.setRandom(chest.getBlockInventory());
                }
            }
        }

        this.getTable().getBlock().setType(Material.ENCHANTMENT_TABLE);

        Bukkit.broadcastMessage(ChatColor.RED + "The Feast has been filled!");
    }
}
