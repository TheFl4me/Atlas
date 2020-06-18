package com.minecraft.plugin.atlas.manager;

import com.minecraft.plugin.atlas.Atlas;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Feast {

    private List<Location> grass;
    private List<Location> air;
    private List<Location> chests;
    private Location table;
    private long spawnTime;

    public Feast() {
        this.spawnTime = System.currentTimeMillis();

        Arena arena = Atlas.getArena();

        int radius = 10;
        int range = arena.getSize() - radius;

        Random r1 = new Random();
        Random r2 = new Random();
        Random r3 = new Random();
        int rX = r1.nextInt(range) - range / 2;
        int rZ = r2.nextInt(range) - range / 2;
        int rY = r3.nextInt(120);

        Location center = new Location(Bukkit.getWorld("world"), rX, rY, rZ);
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

        Bukkit.broadcastMessage(ChatColor.RED + "A feast has spawned at X: " + this.getTable().getBlockX() + ", Z: " + this.getTable().getBlockZ());
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
    }
}
