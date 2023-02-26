package net.daechler.vanillaspawnteleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class VanillaSpawnTeleport extends JavaPlugin implements CommandExecutor {
    private static final int COUNTDOWN_SECONDS = 10;

    @Override
    public void onEnable() {
        getCommand("spawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        World world = Bukkit.getWorlds().get(0); // assume first world is the overworld
        Location spawnLocation = world.getSpawnLocation();

        new CountdownTask(player, spawnLocation).runTaskTimer(this, 0L, 20L);

        return true;
    }

    private static class CountdownTask extends BukkitRunnable {
        private final Player player;
        private final Location destination;
        private final Location initialLocation;
        private int remainingSeconds = COUNTDOWN_SECONDS;

        public CountdownTask(Player player, Location destination) {
            this.player = player;
            this.destination = destination;
            this.initialLocation = player.getLocation();
        }

        @Override
        public void run() {
            if (remainingSeconds == 0) {
                player.teleport(destination);
                cancel();
                return;
            }

            if (player.getLocation().distance(initialLocation) > 1) {
                player.sendMessage("Teleport cancelled due to movement.");
                cancel();
                return;
            }

            String title = String.format("Teleportation in §a%d§f...", remainingSeconds);
            player.sendTitle(title, "", 0, 20, 0);

            remainingSeconds--;
        }
    }
}
