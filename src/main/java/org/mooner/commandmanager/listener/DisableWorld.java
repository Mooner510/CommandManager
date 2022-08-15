package org.mooner.commandmanager.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;

public class DisableWorld implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(BungeeAPI.getServerType(CommandManager.port) != ServerType.MAIN_SERVER) return;
        if (e.getPlayer().getWorld().getName().equals("world")) {
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, -1.5, 0, 0));
            Bukkit.getScheduler().runTaskLater(CommandManager.plugin, () -> e.getPlayer().chat("/is"), 30);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(BungeeAPI.getServerType(CommandManager.port) != ServerType.MAIN_SERVER) return;
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
        if(!e.getDamager().isOp()) return;
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByBlockEvent e) {
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
        e.setCancelled(true);
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
