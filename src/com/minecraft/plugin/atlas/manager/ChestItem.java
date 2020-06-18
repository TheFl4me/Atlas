package com.minecraft.plugin.atlas.manager;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

import static org.bukkit.enchantments.Enchantment.ARROW_DAMAGE;

public enum ChestItem {

    BOW(5, new ItemStack(Material.BOW)),
    ARROW(5, new ItemStack(Material.ARROW)),

    COOKED_PORK_CHOP(5, new ItemStack(Material.GRILLED_PORK)),
    GOLDEN_APPLE(10, new ItemStack(Material.GOLDEN_APPLE)),
    COOKED_BEEF(5, new ItemStack(Material.COOKED_BEEF)),

    COMPASS(2, new ItemStack(Material.COMPASS)),

    EXP_BOTTLE(30, new ItemStack(Material.EXP_BOTTLE)),
    EMERALD(5, new ItemStack(Material.EMERALD)),
    ENDER_PEARL(20, new ItemStack(Material.ENDER_PEARL)),

    BOOK(20, new ItemStack(Material.ENCHANTED_BOOK)),

    HEAL_POT(20, new ItemStack(Material.POTION, 1 , (short) 16421)),
    REGEN_POT(20, new ItemStack(Material.POTION, 1 , (short) 16449)),
    STRENGTH_POT(5, new ItemStack(Material.POTION, 1 , (short) 16393)),
    POISON_POT(5, new ItemStack(Material.POTION, 1 , (short) 16452)),
    SPEED_POT(5, new ItemStack(Material.POTION, 1 , (short) 16418)),
    SLOW_POT(5, new ItemStack(Material.POTION, 1 , (short) 16394)),
    INVIS_POT(5, new ItemStack(Material.POTION, 1 , (short) 16398)),

    DIAMOND_SWORD(10, new ItemStack(Material.DIAMOND_SWORD)),
    DIAMOND_HELMET(5, new ItemStack(Material.DIAMOND_HELMET)),
    DIAMOND_CHEST_PLATE(5, new ItemStack(Material.DIAMOND_CHESTPLATE)),
    DIAMOND_LEGGINGS(5, new ItemStack(Material.DIAMOND_LEGGINGS)),
    DIAMOND_BOOTS(5, new ItemStack(Material.DIAMOND_BOOTS));


    private int spawnChance;
    private ItemStack item;

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

        if (item.getItem().getType() == Material.ENCHANTED_BOOK) {

            Random r3 = new Random();

            List<Enchantment> enchantList = Arrays.asList(Enchantment.values());

            int i = r3.nextInt(enchantList.size());

            Enchantment enchant = enchantList.get(i);

            EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItem().getItemMeta();

            if (book.hasStoredEnchants()) {
                for (Enchantment ench : Enchantment.values()) {
                    if (book.hasStoredEnchant(ench)) {
                        book.removeStoredEnchant(ench);
                    }
                }
            }

            int strength = 3;

            List<Enchantment> strength_2 = Arrays.asList(Enchantment.FIRE_ASPECT, Enchantment.ARROW_KNOCKBACK, Enchantment.KNOCKBACK);
            List<Enchantment> strength_1 = Arrays.asList(Enchantment.ARROW_INFINITE, Enchantment.SILK_TOUCH, Enchantment.ARROW_FIRE, Enchantment.OXYGEN);

            if (strength_1.contains(enchant))
                strength = 1;
            else if(strength_2.contains(enchant))
                strength = 2;

            book.addStoredEnchant(enchant,strength,true);
            item.getItem().setItemMeta(book);
        }



        inv.setItem(r1.nextInt(inv.getSize()), item.getItem());

        if(item.isStackable()) {
            Random r3 = new Random();
            int index = r3.nextInt(7);
            for(int i = 0; i < index; i++)
                inv.addItem(item.getItem());
        }
    }

    public boolean isStackable() {
        switch(this) {
            case ARROW:
            case COOKED_PORK_CHOP:
            case COOKED_BEEF:
            case EMERALD:
            case EXP_BOTTLE:
                return true;
            default:
                return false;
        }
    }
}
