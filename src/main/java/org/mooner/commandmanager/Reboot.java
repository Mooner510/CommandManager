package org.mooner.commandmanager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;

import static org.mooner.moonerbungeeapi.api.Rank.chat;

public class Reboot {
    public static void reboot(ServerType type) {
        if(type == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(chat("&b서버 리붓"), chat("&c30초 &6후 &a서버를 재시작 합니다."), 0, 410, 0);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(chat("&b서버 리붓"), chat("&c10초 &6후 &a서버를 재시작 합니다."), 0, 410, 0);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 400);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(" ", chat("&c5초 &6후 &a서버를 재시작 합니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 500);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(" ", chat("&c4초 &6후 &a서버를 재시작 합니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 520);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(" ", chat("&c3초 &6후 &a서버를 재시작 합니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 540);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(" ", chat("&c2초 &6후 &a서버를 재시작 합니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 560);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(" ", chat("&c1초 &6후 &a서버를 재시작 합니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 580);
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(chat("&b서버 리붓"), chat("&c30초 &6후 &a" + type.getTag() + "로 이동됩니다."), 0, 410, 0);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(chat("&b서버 리붓"), chat("&c10초 &6후 &a" + type.getTag() + "로 이동됩니다."), 0, 410, 0);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 400);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("", chat("&c5초 &6후 &a" + type.getTag() + "로 이동됩니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 500);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("", chat("&c4초 &6후 &a" + type.getTag() + "로 이동됩니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 520);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("", chat("&c3초 &6후 &a" + type.getTag() + "로 이동됩니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 540);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("", chat("&c2초 &6후 &a" + type.getTag() + "로 이동됩니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 560);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("", chat("&c1초 &6후 &a" + type.getTag() + "로 이동됩니다."), 5, 30, 20);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }, 580);
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    BungeeAPI.sendBungeePlayer(p.getName(), type);
                }
            }, 600);
        }
        Bukkit.getScheduler().runTaskLater(CommandManager.plugin, Bukkit::shutdown, 620);
    }
}