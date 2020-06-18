package com.minecraft.plugin.atlas.utils;

import com.minecraft.plugin.atlas.Atlas;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {


    private static Server server = null;
    private int number = 1;

    public static Server get() {
        return server;
    }

    public Server() {
        server = this;
    }

    public void initiate() {

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Atlas.getPlugin(), new Lag(), 100L, 1L);

    }


    public boolean isLagging() {
        return this.getTPS() < 15;
    }

    public double getTPS() {
        return Lag.getTPS();
    }

    public double getLagPercentage() {
        return Math.round((1.0D - this.getTPS() / 20.0D) * 100.0D);
    }


    public String getDate() {
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return date.format(new Date());
    }

    public String getDate(long ms) {
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return date.format(new Date(ms));
    }

    public String getTimeUntil(long epoch) {
        epoch -= System.currentTimeMillis();
        return this.getTime(epoch);
    }

    public String getTime(long ms) {
        long sec = ms / Unit.SECONDS.toSeconds();

        final long unitMin = Unit.MINUTES.toSeconds();
        final long unitHours = Unit.HOURS.toSeconds();
        final long unitDays = Unit.DAYS.toSeconds();
        final long unitWeeks = Unit.WEEKS.toSeconds();
        final long unitMonths = Unit.MONTHS.toSeconds();
        final long unitYears = Unit.YEARS.toSeconds();

        StringBuilder sb = new StringBuilder(40);
        long years = sec / unitYears;
        if (years > 0) {
            sb.append(years).append(years == 1 ? " year " : " years ");
            sec -= years * unitYears;
        }
        long months = sec / unitMonths;
        if (months > 0) {
            sb.append(months).append(months == 1 ? " month " : " months ");
            sec -= months * unitMonths;
        }
        long weeks = sec / unitWeeks;
        if (weeks > 0) {
            sb.append(weeks).append(weeks == 1 ? " week " : " weeks ");
            sec -= weeks * unitWeeks;
        }
        long days = sec / unitDays;
        if (days > 0) {
            sb.append(days).append(days == 1 ? " day " : " days ");
            sec -= days * unitDays;
        }
        long hours = sec / unitHours;
        if (hours > 0) {
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
            sec -= hours * unitHours;
        }
        long minutes = sec / unitMin;
        if (minutes > 0) {
            sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
            sec -= minutes * unitMin;
        }
        if (sec > 0) {
            sb.append(sec).append(sec == 1 ? " second " : " seconds ");
        }
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb = new StringBuilder("N/A");
        }
        return sb.toString();
    }

    public String getTimeDigitalUntil(long epoch) {
        epoch -= System.currentTimeMillis();
        return this.getTimeDigital(epoch);
    }

    public String getTimeDigital(long ms) {
        StringBuilder sb = new StringBuilder(40);
        long sec = ms / Unit.SECONDS.toSeconds();

        final long unitMin = Unit.MINUTES.toSeconds();

        long minutes = sec / unitMin;
        if (minutes > 0) {
            sb.append(minutes < 10 ? "0" + minutes + ":" : minutes + ":");
            sec -= minutes * unitMin;
        } else {
            sb.append("00:");
        }

        if (sec > 0) {
            sb.append(sec < 10 ? "0" + sec : sec);
        } else
            sb.append("00");

        if (sb.length() < 1)
            sb = new StringBuilder("--:--");
        return sb.toString();
    }

    public int getPing(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        EntityPlayer ep = cp.getHandle();
        return ep.ping;
    }
}
