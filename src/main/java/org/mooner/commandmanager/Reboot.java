package org.mooner.commandmanager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;

import java.util.Collection;

import static org.mooner.moonerbungeeapi.api.Rank.chat;

public class Reboot {
    private static int time;
    private static ServerType serverType = null;

    public static String timeColor() {
        if(time <= 5) return ChatColor.RED.toString() + time;
        else if(time <= 10) return ChatColor.GOLD.toString() + time;
        else if(time <= 30) return ChatColor.YELLOW.toString() + time;
        else return ChatColor.GRAY.toString() + time;
    }

    public static void reboot(ServerType type) {
        serverType = type;
        time = 60;
        Bukkit.getPluginManager().registerEvents(new RebootBlocker(), CommandManager.plugin);
        Bukkit.getScheduler().runTaskTimer(CommandManager.plugin, task -> {
            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            if(time <= 0) {
                for (Player p : players) {
                    p.sendTitle(" ", chat("&6이동중..."), 0, 15, 10);
                    BungeeAPI.sendBungeePlayer(p.getName(), type);
                }
                Bukkit.getScheduler().runTaskTimer(CommandManager.plugin, task2 -> {
                    if (Bukkit.getOnlinePlayers().isEmpty()) {
                        Bukkit.getScheduler().runTaskLater(CommandManager.plugin, Bukkit::shutdown, 20);
                        task2.cancel();
                    }
                }, 20, 20);
                task.cancel();
            } else {
                for (Player p : players) {
                    p.sendTitle(chat("&b서버 리붓"), chat(timeColor() + "초 &a후 " + type.getTag() + "로 이동됩니다."), 0, 20, 10);
                }
                if (time == 60) {
                    for (Player p : players) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
                    }
                } else if (time == 30 || time == 10) {
                    for (Player p : players) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                } else if (time <= 5) {
                    for (Player p : players) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
                } else {
                    for (Player p : players) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.075f, 0.75f);
                }
            }
            time--;
        }, 0, 20);
    }

    private static class RebootBlocker implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            e.getPlayer().sendMessage(chat("&c곧 "+CommandManager.serverType.getTag()+"가 재시작되기 때문에 해당 서버에 참여하실 수 없습니다. &e자동으로 " + serverType.getTag() + "로 이동되었습니다."));
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 0.75f, 1f);
            BungeeAPI.sendBungeePlayer(e.getPlayer(), serverType);
        }

        private String namer(InventoryType type) {
            return switch (type) {
                case CHEST -> "상자를";
                case DISPENSER -> "발사기를";
                case DROPPER -> "공급기를";
                case FURNACE -> "화로를";
                case WORKBENCH -> "작업대를";
//                case CRAFTING, PLAYER -> "플레이어 인벤토리를";
                case ENCHANTING -> "마법부여대를";
                case BREWING -> "양조기를";
//                case CREATIVE -> "크리에이티브 인벤토리를";
                case MERCHANT -> "주민을";
                case ENDER_CHEST -> "엔더 상자를";
                case ANVIL -> "모루를";
                case SMITHING -> "대장장이 작업대를";
                case BEACON -> "신호기를";
                case HOPPER -> "깔대기를";
                case SHULKER_BOX -> "셜커 상자를";
                case BARREL -> "통을";
                case BLAST_FURNACE -> "용광로를";
//                case LECTERN -> "독서대를";
                case SMOKER -> "훈연기를";
                case LOOM -> "베틀을";
                case CARTOGRAPHY -> "지도 제작대를";
                case GRINDSTONE -> "숫돌을";
                case STONECUTTER -> "석재 절단기를";
//                case COMPOSTER -> "퇴비통을";
                default -> null;
            };
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if(e.getInventory().getLocation() != null) {
                final Player player = (Player) e.getPlayer();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat("&c아이템 사라짐 방지를 위해 "+namer(e.getInventory().getType())+" 사용할 수 없습니다.")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.75f, 1f);
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            final Player player = (Player) e.getWhoClicked();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat("&c아이템 사라짐 방지를 위해 아이템을 옮길 수 없습니다.")));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.75f, 1f);
            e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryMove(InventoryDragEvent e) {
            Player player = (Player) e.getWhoClicked();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat("&c아이템 사라짐 방지를 위해 아이템을 옮길 수 없습니다.")));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.75f, 1f);
            e.setCancelled(true);
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent e) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat("&c아이템 사라짐 방지를 위해 아이템을 버릴 수 없습니다.")));
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 0.75f, 1f);
            e.setCancelled(true);
        }
    }
}
