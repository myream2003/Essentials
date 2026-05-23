package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WeatherCommand extends AbstractCommand {

    public WeatherCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.weather")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/weather <clear|rain|thunder> [duration] [world]");
            return;
        }

        World world = resolveWorld(sender, args.length >= 3 ? args[2] : null);
        if (world == null) return;

        // Duration in ticks (default 30 seconds = 600 ticks)
        int durationTicks = 600;
        if (args.length >= 2) {
            try {
                durationTicks = Integer.parseInt(args[1]) * 20;
            } catch (NumberFormatException ignored) {}
        }

        switch (args[0].toLowerCase()) {
            case "clear", "sun", "sunny" -> {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(durationTicks);
                send(sender, "weather-set", world.getName(), "clear");
            }
            case "rain", "rainy" -> {
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(durationTicks);
                send(sender, "weather-set", world.getName(), "rain");
            }
            case "thunder", "storm" -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(durationTicks);
                world.setThunderDuration(durationTicks);
                send(sender, "weather-set", world.getName(), "thunder");
            }
            default -> sendRaw(sender, "invalid-usage", "/weather <clear|rain|thunder>");
        }
    }

    private World resolveWorld(CommandSender sender, String worldName) {
        if (worldName != null) {
            World w = Bukkit.getWorld(worldName);
            if (w == null) send(sender, "player-not-found", worldName);
            return w;
        }
        if (sender instanceof Player p) return p.getWorld();
        List<World> worlds = Bukkit.getWorlds();
        if (worlds.isEmpty()) {
            sender.sendMessage("No worlds loaded.");
            return null;
        }
        return worlds.get(0);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return List.of("clear", "rain", "thunder");
        return List.of();
    }
}
