package net.daechler.spawnteleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class SpawnTeleport extends JavaPlugin implements CommandExecutor {

    private HashMap<UUID, BukkitRunnable> teleportingPlayers = new HashMap<>();
    private int countdownTime;

    @Override
    public void onEnable() {
        this.getCommand("spawn").setExecutor(this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + getName() + " has been enabled!");

        // Save the default config if it does not exist
        this.saveDefaultConfig();

        // Read countdown time from the config
        countdownTime = this.getConfig().getInt("countdownTime", 10);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + getName() + " has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location initialLocation = player.getLocation().getBlock().getLocation();

                BukkitRunnable teleportCountdown = new BukkitRunnable() {
                    int countdown = countdownTime;

                    @Override
                    public void run() {
                        if (countdown <= 0) {
                            player.teleport(player.getWorld().getSpawnLocation());
                            player.sendMessage(ChatColor.GREEN + "Teleported to spawn!");
                            teleportingPlayers.remove(player.getUniqueId());
                            this.cancel();
                        } else if (!player.getLocation().getBlock().getLocation().equals(initialLocation)) {
                            player.sendMessage(ChatColor.RED + "Teleportation cancelled because you moved!");
                            teleportingPlayers.remove(player.getUniqueId());
                            this.cancel();
                        } else {
                            player.sendTitle(ChatColor.YELLOW + "" + countdown--, "", 10, 20, 10);
                        }
                    }
                };

                teleportCountdown.runTaskTimer(this, 0, 20);
                teleportingPlayers.put(player.getUniqueId(), teleportCountdown);
                return true;
            }
        }
        return false;
    }
}
