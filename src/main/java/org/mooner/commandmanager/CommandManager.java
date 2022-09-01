package org.mooner.commandmanager;

import com.google.common.collect.ImmutableSet;
import de.epiceric.shopchest.event.ShopCreateEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.commandmanager.listener.DisableWorld;
import org.mooner.commandmanager.shop.ShopDistance;
import org.mooner.moonerbungeeapi.api.BungeeAPI;
import org.mooner.moonerbungeeapi.api.ServerType;
import org.mooner.moonerbungeeapi.db.PlayerDB;
import org.mooner.moonereco.API.EcoAPI;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.mooner.moonerbungeeapi.api.Rank.chat;

public final class CommandManager extends JavaPlugin implements Listener {
    public static CommandManager plugin;
    public static ServerType serverType;

    private static HashSet<String> allowedCommands;
    private static HashSet<Material> bannedItem;
    private static HashMap<Material, ShopDistance> shopData;
    public static final String dataPath = "../db/";

    private void setWorldDifficulty(World w, Difficulty d) {
        if(w == null) return;
        w.setKeepSpawnInMemory(false);
        w.setDifficulty(d);
        Bukkit.getConsoleSender().sendMessage(chat("&6Set Difficulty to "+d+" in world " + w.getName()));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        serverType = BungeeAPI.getServerType(Bukkit.getPort());
        getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        switch (serverType) {
            case MAIN_SERVER, SPAWN_SERVER -> {
                Bukkit.getPluginManager().registerEvents(new DisableWorld(), this);
                for (World w : Bukkit.getWorlds()) setWorldDifficulty(w, Difficulty.PEACEFUL);
            }
            default -> {
                for (World w : Bukkit.getWorlds()) setWorldDifficulty(w, Difficulty.HARD);
            }
        }
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
        final ConfigurationSection g = config.getConfigurationSection("global");
        if(g != null) allowedCommands.addAll(g.getKeys(false));
        final ConfigurationSection css = config.getConfigurationSection("server." + BungeeAPI.getServerType(Bukkit.getServer().getPort()).getTag());
        if(css != null) allowedCommands.addAll(css.getKeys(false));

        FileConfiguration items = loadConfig(dataPath, "banItem.yml");
        bannedItem = new HashSet<>();
        final ConfigurationSection global = items.getConfigurationSection("global");
        if(global != null) bannedItem.addAll(global.getKeys(false).stream()
                .map(Material::valueOf)
                .toList());
        final ConfigurationSection cs = items.getConfigurationSection("server." + BungeeAPI.getServerType(Bukkit.getServer().getPort()).getTag());
        if(cs != null) bannedItem.addAll(cs.getKeys(false).stream()
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommandRun(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        final String[] s = e.getMessage().substring(1).split(" ");
        if(s[0].equals("is") || s[0].equals("island")) {
            if(serverType != ServerType.MAIN_SERVER) {
                e.setCancelled(true);
                BungeeAPI.sendBungeePlayer(e.getPlayer().getName(), ServerType.MAIN_SERVER);
//                e.getPlayer().sendMessage(ChatColor.RED + "해당 명령어는 메인 서버에서만 사용 가능합니다.");
                return;
            }
        }
        if(!e.getPlayer().isOp() && !allowedCommands.contains(s[0])) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "해당 명령어는 사용할 수 없습니다! 관리자에게 문의하세요.");
        }
    }

//    @EventHandler
//    public void onServerChange()

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

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(e.getPlayer().isOp()) return;
        if(serverType == ServerType.SPAWN_SERVER) {
            final Location loc = new Location(Bukkit.getWorld("world"), 0.5, 65, 0.5);
            e.getPlayer().teleport(loc);
            Bukkit.getScheduler().runTaskLater(this, () -> {
                e.getPlayer().teleport(loc);
                if (PlayerDB.init.isTutorial(e.getPlayer())) {
                    e.getPlayer().teleport(loc);
                } else {
                    e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, -55, 0.5, -90, 0));
                    e.getPlayer().sendMessage("",
                            chat("  &6서버에 오신 것을 환영합니다!"),
                            chat("  &a튜토리얼을 모두 마치신 후, 서버를 플레이 하실 수 있습니다."),
                            ""
                    );

                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.8f, 0.75f);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2f);
                    e.getPlayer().sendTitle(chat("&6튜토리얼"), chat("&e완료 후 서버를 플레이 하실 수 있습니다."), 20, 120, 40);
                }
            }, 40);
        } else if(serverType == ServerType.SURVIVAL_SERVER) {
            e.getPlayer().sendMessage("",
                    chat("  &b/rtp &e명령어로 렌덤 좌표로 순간이동 할 수 있습니다."),
                    ""
            );
        } else {
            if (!PlayerDB.init.isTutorial(e.getPlayer())) {
                BungeeAPI.sendBungeePlayer(e.getPlayer().getName(), ServerType.SPAWN_SERVER);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(serverType != ServerType.SPAWN_SERVER && !e.getPlayer().getWorld().getName().equals("world")) return;
        final Location to;
        if((to = e.getTo()) != null) {
            if(to.getY() <= -50 && to.getY() >= -60) {
                Location loc = to.clone().add(0, -1, 0);
                final Block b = loc.getBlock();
                if (b.getType() == Material.BEEHIVE) {
                    if (!PlayerDB.init.isTutorial(e.getPlayer())) {
                        PlayerDB.init.setTutorial(e.getPlayer(), true);
                        e.getPlayer().sendMessage(
                                "",
                                chat("  &6튜토리얼을 완료 했습니다!"),
                                chat("  &e튜토리얼 완료 보상으로 지원금 &63000원&e이 지급되었습니다."),
                                chat("  &a즐거운 시간 되세요!"),
                                ""
                        );
                        EcoAPI.init.addPay(e.getPlayer(), 3000);
                    } else {
                        e.getPlayer().sendMessage(
                                "",
                                chat("  &6튜토리얼을 완료 했습니다!"),
                                chat("  &a즐거운 시간 되세요!"),
                                ""
                        );
                    }
                    e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 64, 0.5, -90, 0));
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.8f, 1.5f);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0.5f);
                    e.getPlayer().sendTitle(chat("&6튜토리얼 완료!"), chat("&eLite24&f에 오신 것을 환영합니다!"), 20, 120, 40);
                }
            }
        }
    }

    private static final ImmutableSet<Material> helmets = ImmutableSet.of(Material.CHAINMAIL_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET, Material.LEATHER_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET, Material.CARVED_PUMPKIN, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.ZOMBIE_HEAD, Material.PLAYER_HEAD);

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
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            } else {
                BungeeAPI.sendBungeePlayer(p.getName(), ServerType.SPAWN_SERVER);
            }
            return true;
        } else if(command.getName().equals("survival")) {
            if(!(sender instanceof Player p)) return true;
            if (BungeeAPI.getServerType(Bukkit.getServer().getPort()) == ServerType.SURVIVAL_SERVER) {
                p.sendMessage(chat("&c현재 접속하신 서버가 야생 서버입니다!"));
            } else {
                BungeeAPI.sendBungeePlayer(p.getName(), ServerType.SURVIVAL_SERVER);
            }
            return true;
        } else if(command.getName().equals("reboot")) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(chat("  &6서버 재시작 안내"));
            Bukkit.broadcastMessage(chat("  &f서버가 &a60초 &f후에 재시작됩니다."));
            if(args.length == 0) {
                switch (serverType) {
                    case MAIN_SERVER, SPAWN_SERVER -> Reboot.reboot(ServerType.SURVIVAL_SERVER);
                    case SURVIVAL_SERVER -> Reboot.reboot(ServerType.SPAWN_SERVER);
                }
            } else {
                try {
                    final ServerType type = ServerType.valueOf(args[0]);
                    if (type == serverType) {
                        sender.sendMessage(chat("&c같은 서버로 옮길 수 없습니다!"));
                    } else Reboot.reboot(type);
                } catch (Exception e) {
                    Bukkit.broadcastMessage(chat("  &e사유: &f" + String.join(" ", args)));
                    switch (serverType) {
                        case MAIN_SERVER, SPAWN_SERVER -> Reboot.reboot(ServerType.SURVIVAL_SERVER);
                        case SURVIVAL_SERVER -> Reboot.reboot(ServerType.SPAWN_SERVER);
                    }
                }
            }
            Bukkit.broadcastMessage("");
            return true;
        } else if(command.getName().equals("상점")) {
            if(!(sender instanceof Player p)) return true;
            if (BungeeAPI.getServerType(Bukkit.getServer().getPort()) == ServerType.SPAWN_SERVER) {
                p.teleport(new Location(Bukkit.getWorld("world"), -16.5, 2, -150.5, 90, 0));
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            } else {
                p.sendMessage(chat("&c스폰 서버에서만 사용 가능합니다!"));
            }
            return true;
        } else if(command.getName().equals("tutorial")) {
            if(!(sender instanceof Player p)) return true;
            if (BungeeAPI.getServerType(Bukkit.getServer().getPort()) == ServerType.SPAWN_SERVER) {
                p.teleport(new Location(Bukkit.getWorld("world"), 0.5, -55, 0.5, -90, 0));
                p.sendMessage("",
                        chat("  &6서버에 오신 것을 환영합니다!"),
                        chat("  &a튜토리얼을 모두 마치신 후, 서버를 플레이 하실 수 있습니다."),
                        "");
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.8f, 0.75f);
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2f);
                    p.sendTitle(chat("&6튜토리얼"), chat("&e완료 후 서버를 플레이 하실 수 있습니다."), 20, 120, 40);
                }, 20);
            } else {
                p.sendMessage(chat("&c스폰 서버에서만 사용 가능합니다!"));
            }
            return true;
        } else if(command.getName().equals("command")) {
            FileConfiguration config = loadConfig(dataPath, "commands.yml");
            allowedCommands = new HashSet<>();
            final ConfigurationSection g = config.getConfigurationSection("global");
            if(g != null) allowedCommands.addAll(g.getKeys(false));
            final ConfigurationSection css = config.getConfigurationSection("server." + BungeeAPI.getServerType(Bukkit.getServer().getPort()).getTag());
            if(css != null) allowedCommands.addAll(css.getKeys(false));
            return true;
        } else if(command.getName().equals("hat")) {
            if(!(sender instanceof Player p)) return true;
            final ItemStack i = p.getInventory().getItemInMainHand().clone();
            if(helmets.contains(i.getType())) {
                p.sendMessage(chat("&c그건 직접 머리에 쓸 수 있지 않나요?"));
                return true;
            }
            final ItemStack helmet = p.getInventory().getHelmet();
            if(helmet != null && helmet.getEnchantmentLevel(Enchantment.BINDING_CURSE) > 0) {
                p.sendMessage(chat("&c이런 이런.. 뺄 수 없는 아이템을 이 명령어로 빼려고 하지 마세요. &b/귀속저주&c가 있잖아요?"));
                return true;
            }
            if(i.getType() == Material.AIR) {
                if(helmet == null || helmet.getType() == Material.AIR) {
                    p.sendMessage(chat("&c손에 아이템을 들어주세요!"));
                } else {
                    p.sendMessage(chat("&a모자를 뺐습니다."));
                }
                return true;
            }
            p.getInventory().setItemInMainHand(helmet);
            p.getInventory().setHelmet(i);
            p.sendMessage(chat("&a멋진 모자네요!"));
            return true;
        }
        return false;
    }

    private final List<String> servers = Arrays.stream(ServerType.values()).map(Enum::toString).toList();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("reboot".equals(command.getName())) {
            if (args.length == 1) {
                return servers.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
            }
        }
        return null;
    }
}
