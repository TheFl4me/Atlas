package com.minecraft.plugin.atlas.manager;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum ChestItem {

    BOW(25, new ItemStack(Material.BOW)),
    ARROW(30, new ItemStack(Material.ARROW)),

    BREAD(30, new ItemStack(Material.BREAD)),
    COOKED_PORK_CHOP(15, new ItemStack(Material.GRILLED_PORK)),
    GOLDEN_APPLE(3, new ItemStack(Material.GOLDEN_APPLE)),
    COOKED_BEEF(15, new ItemStack(Material.COOKED_BEEF)),

    COMPASS(5, new ItemStack(Material.COMPASS)),
    FISHING_ROD(20, new ItemStack(Material.FISHING_ROD)),

    DIAMOND(20, new ItemStack(Material.DIAMOND)),

    EXP_BOTTLE(40, new ItemStack(Material.EXP_BOTTLE)),
    LAPIS_LAZULI(10, new ItemStack(Material.INK_SACK, 1, (short) 4)),
    ENDER_PEARL(20, new ItemStack(Material.ENDER_PEARL)),

    DIAMOND_SWORD(20, new ItemStack(Material.DIAMOND_SWORD)),
    DIAMOND_AXE(10, new ItemStack(Material.DIAMOND_AXE)),
    DIAMOND_HELMET(10, new ItemStack(Material.DIAMOND_HELMET)),
    DIAMOND_CHEST_PLATE(10, new ItemStack(Material.DIAMOND_CHESTPLATE)),
    DIAMOND_LEGGINGS(10, new ItemStack(Material.DIAMOND_LEGGINGS)),
    DIAMOND_BOOTS(10, new ItemStack(Material.DIAMOND_BOOTS));


    private int spawnChance;
    private ItemStack item;

    private static final Collection<ChestItem> SPECIAL_ITEMS = Arrays.asList(DIAMOND_AXE, DIAMOND_BOOTS, DIAMOND_CHEST_PLATE, DIAMOND_HELMET, DIAMOND_LEGGINGS, DIAMOND_SWORD);

    ChestItem(int chance, ItemStack item) {
        this.spawnChance = chance;
        this.item = item;
    }

    public static List<ChestItem> getAllItems() {
        List<ChestItem> list = new ArrayList<>();
        for(ChestItem item : values()) {
            for(int i = 0; i < item.getSpawnChance(); i++) {
                list.add(item);
            }
        }
        return list;
    }

    public int getSpawnChance() {
        return this.spawnChance;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public static void setRandom(Inventory inv) {
        List<ChestItem> items = getAllItems();
        Random r1 = new Random();
        Random r2 = new Random();
        ChestItem item = items.get(r2.nextInt(items.size()));
        inv.setItem(r1.nextInt(inv.getSize()), item.getItem());
        if(item.isStackable()) {
            Random r3 = new Random();
            int index = r3.nextInt(64);
            for(int i = 0; i < index; i++)
                inv.addItem(item.getItem());
        }
    }

    public boolean isStackable() {
        switch(this) {
            case ARROW:
            case BREAD:
            case COOKED_PORK_CHOP:
            case COOKED_BEEF:
            case LAPIS_LAZULI:
            case EXP_BOTTLE:
                return true;
            default:
                return false;
        }
    }
}
