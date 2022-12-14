package org.mooner.commandmanager.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.mooner.commandmanager.CommandManager;
import org.mooner.moonerbungeeapi.api.ServerType;

import java.util.HashMap;
import java.util.UUID;

public class DisableWorld implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(CommandManager.serverType != ServerType.MAIN_SERVER) return;
        if (e.getPlayer().getWorld().getName().equals("world")) {
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, -1.5, 0, 0));
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> e.getPlayer().chat("/is"), 30);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(CommandManager.serverType != ServerType.MAIN_SERVER) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        if(e.getFrom().getY() <= 40 || e.getFrom().getX() > 100 || e.getFrom().getX() < -100 || e.getFrom().getZ() > 100 || e.getFrom().getZ() < -100) {
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, -1.5, 0, 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMultiPlace(BlockMultiPlaceEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketEmptyEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if(!e.getBlock().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if(!e.getBlock().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.LECTERN) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLectern(PlayerTakeLecternBookEvent e) {
        if(e.getPlayer().isOp()) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTeleport(PlayerTeleportEvent e) {
        if(e.getPlayer().isOp()) return;
        if(!e.getPlayer().getWorld().getName().startsWith("world")) return;
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT && e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLightning(LightningStrikeEvent e) {
        if(!e.getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpread(BlockSpreadEvent e) {
        if (e.getBlock().getType() == Material.FIRE) {
            if(!e.getBlock().getWorld().getName().startsWith("world")) return;
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onForm(BlockFormEvent e) {
        if (e.getBlock().getType() == Material.SNOW) {
            if(!e.getBlock().getWorld().getName().startsWith("world")) return;
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            e.setCancelled(true);
            return;
        }
        if(e.getDamager().isOp()) return;
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByBlockEvent e) {
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    private static final HashMap<UUID, Long> teleportTime = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
        if(CommandManager.serverType == ServerType.SPAWN_SERVER) {
            if (e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                if (e.getEntity() instanceof Player p) {
                    final Long time = teleportTime.get(p.getUniqueId());
                    if(time == null || time + 3000 <= System.currentTimeMillis()) {
                        p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, 0.5, -90, 0));
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        p.setFireTicks(0);
                        teleportTime.put(p.getUniqueId(), System.currentTimeMillis());
                        Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> p.setFireTicks(0), 2);
                    }
                }
            }
        }
    }

//    @EventHandler(priority = EventPriority.HIGH)
//    public void onSpawn(EntitySpawnEvent e) {
//        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
//        e.setCancelled(true);
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockChange(EntityChangeBlockEvent e) {
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }
}
