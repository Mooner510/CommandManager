package org.mooner.commandmanager;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class CommandManager extends JavaPlugin implements Listener {
    private static ImmutableSet<String> allowedCommands;
    public static final String dataPath = "plugins/CommandManager/";

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
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
        File f = new File(dataPath, "config.yml");
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
                this.getLogger().info("성공적으로 config.yml을(를) 생성했습니다.");
            } catch (IOException e) {
                e.printStackTrace();
                this.getLogger().warning("config.yml을(를) 생성하지 못했습니다.");
            }
        }
        FileConfiguration config = loadConfig(dataPath, "config.yml");
        allowedCommands = ImmutableSet.copyOf(config.getStringList("commands"));
    }

    @EventHandler
    public void onCommandRun(PlayerCommandPreprocessEvent e) {
        if(!e.getPlayer().isOp() && !allowedCommands.contains(e.getMessage().substring(1))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "해당 명령어는 사용할 수 없습니다! 관리자에게 문의하세요.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("reloadcommand")) {
            reload();
            return true;
        }
        return false;
    }
}
