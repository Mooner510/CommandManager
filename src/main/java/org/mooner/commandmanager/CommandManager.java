package org.mooner.commandmanager;

import de.epiceric.shopchest.event.ShopCreateEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.commandmanager.shop.ShopDistance;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

import static org.mooner.moonerbungeeapi.api.Rank.chat;

public final class CommandManager extends JavaPlugin implements Listener {
    public static CommandManager plugin;

    private static HashSet<String> allowedCommands;
    private static HashSet<Material> bannedItem;
    private static HashMap<Material, ShopDistance> shopData;
    public static final String dataPath = "../db/";

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin Disabled!");
    }

    public static FileConfiguration loadConfig(String Path, String File) {
        FileInputStream stream = null;
        File f = new File(Path, File);

        try {
            stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert stream != null;
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public void reload() {
        new File(dataPath).mkdirs();
        File f = new File(dataPath, "commands.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
                InputStream i = this.getClass().getResourceAsStream("/config.yml");
                OutputStream o = Files.newOutputStream(f.toPath());

                int length;
                byte[] buffer = new byte[1024];

                while (i != null && (length = i.read(buffer)) > 0) o.write(buffer, 0, length);
                o.flush();
                o.close();
                if(i != null) i.close();
                this.getLogger().info("성공적으로 commands.yml을(를) 생성했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                this.getLogger().warning("commands.yml을(를) 생성하지 못했습니다.");
            }
        }
        File f2 = new File(dataPath, "banItem.yml");
        if(!f2.exists()) {
            try {
                f2.createNewFile();
                InputStream i = this.getClass().getResourceAsStream("/config.yml");
                OutputStream o = Files.newOutputStream(f2.toPath());

                int length;
                byte[] buffer = new byte[1024];

                while (i != null && (length = i.read(buffer)) > 0) o.write(buffer, 0, length);
                o.flush();
                o.close();
                if(i != null) i.close();
                this.getLogger().info("성공적으로 banItem.yml을(를) 생성했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                this.getLogger().warning("banItem.yml을(를) 생성하지 못했습니다.");
            }
        }
        File f3 = new File(dataPath, "shop.yml");
        if(!f3.exists()) {
            try {
                f3.createNewFile();
                InputStream i = this.getClass().getResourceAsStream("/shop.yml");
                OutputStream o = Files.newOutputStream(f3.toPath());

                int length;
                byte[] buffer = new byte[1024];

                while (i != null && (length = i.read(buffer)) > 0) o.write(buffer, 0, length);
                o.flush();
                o.close();
                if(i != null) i.close();
                this.getLogger().info("성공적으로 shop.yml을(를) 생성했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                this.getLogger().warning("shop.yml을(를) 생성하지 못했습니다.");
            }
        }
        FileConfiguration config = loadConfig(dataPath, "commands.yml");
        allowedCommands = new HashSet<>();
        allowedCommands.addAll(config.getStringList("global"));
        allowedCommands.addAll(config.getStringList("server."+BungeeAPI.getServerType(Bukkit.getServer().getPort()).getTag()));

        FileConfiguration items = loadConfig(dataPath, "banItem.yml");
        bannedItem = new HashSet<>();
        bannedItem.addAll(items.getStringList("global").stream()
                .map(Material::valueOf)
                .toList());
        bannedItem.addAll(items.getStringList("server."+BungeeAPI.getServerType(Bukkit.getServer().getPort()).getTag()).stream()
                .map(Material::valueOf)
                .toList());

        FileConfiguration shops = loadConfig(dataPath, "shop.yml");
        shopData = new HashMap<>();
        final ConfigurationSection c = shops.getConfigurationSection("items");
        if(c != null) for (String key : c.getKeys(false)) {
            shopData.put(Material.valueOf(key), new ShopDistance(c.getDouble("sell.min"), c.getDouble("sell.max"), c.getDouble("buy.min"), c.getDouble("buy.max")));
        }
    }

    @EventHandler
    public void onCreateShop(ShopCreateEvent e) {
        if(e.isCancelled() || e.getPlayer().isOp()) return;
        ShopDistance shop = shopData.get(e.getShop().getItem().getItemStack().getType());
        if(shop == null) return;
        if(!shop.checkBuy(e.getShop().getBuyPrice())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chat("&c구매 가격은 &6" + shop.getMinBuy() + " &c~ " + shop.getMaxBuy() + "&c원 사이여야 합니다."));
        } else if(!shop.checkSell(e.getShop().getSellPrice())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chat("&c판매 가격은 &6" + shop.getMinSell() + " &c~ " + shop.getMaxSell() + "&c원 사이여야 합니다."));
        }
    }

    @EventHandler
    public void onCommandRun(PlayerCommandPreprocessEvent e) {
        final String[] s = e.getMessage().substring(1).split(" ");
        if(!e.getPlayer().isOp() && !allowedCommands.contains(s[0])) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "해당 명령어는 사용할 수 없습니다! 관리자에게 문의하세요.");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.isCancelled() || e.getPlayer().isOp()) return;
        if (bannedItem.contains(e.getBlockPlaced().getType())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(chat("&c설치하신 아이템은 서버에서 사용 불가능한 아이템입니다."));
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.isCancelled() || e.getWhoClicked().isOp() || e.getCurrentItem() == null) return;
        if (bannedItem.contains(e.getCurrentItem().getType())) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage(chat("&c조합하시려는 아이템은 서버에서 사용 불가능한 아이템입니다."));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("reloadcommand")) {
            sender.sendMessage("Reload Complete.");
            reload();
            return true;
        } else if(command.getName().equals("spawn")) {
            if(!(sender instanceof Player p)) return true;
            if (BungeeAPI.getServerType(Bukkit.getServer().getPort()) == ServerType.SPAWN_SERVER) {
                p.teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, 0.5, -90, 0));
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            } else {
                BungeeAPI.sendBungeePlayer(p.getName(), ServerType.SPAWN_SERVER);
            }
            return true;
        } else if(command.getName().equals("tutorial")) {
            if(!(sender instanceof Player p)) return true;
            if (BungeeAPI.getServerType(Bukkit.getServer().getPort()) == ServerType.SPAWN_SERVER) {
                p.teleport(new Location(Bukkit.getWorld("world"), 0.5, -56, 0.5, -90, 0));
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            } else {
                p.sendMessage(chat("&c스폰 서버에서만 사용 가능합니다!"));
            }
            return true;
        } else if(command.getName().equals("hat")) {
            if(!(sender instanceof Player p)) return true;
            final ItemStack i = p.getInventory().getItemInMainHand().clone();
            if(i.getType() == Material.AIR) {
                p.sendMessage(chat("&c손에 아이템을 들어주세요!"));
                return true;
            }
            p.getInventory().setItemInMainHand(p.getInventory().getHelmet());
            p.getInventory().setHelmet(i);
            p.sendMessage(chat("&a멋진 모자네요!"));
            return true;
        }
        return false;
    }
}
