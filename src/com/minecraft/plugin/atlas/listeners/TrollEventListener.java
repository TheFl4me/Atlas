package com.minecraft.plugin.atlas.listeners;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.Random;

public class TrollEventListener implements Listener {

    @EventHandler
    public void creeperSound(BlockBreakEvent event) {
        Block block = event.getBlock();
        Random r = new Random();
        int i = r.nextInt(100);
        if (i < 5) {
            Player player = event.getPlayer();
            switch (block.getType()) {
                case EMERALD_ORE:
                case DIAMOND_ORE:
                    player.playSound(player.getLocation(), Sound.CREEPER_HISS, 10, 1);
                    break;
            }
        }
    }
}
