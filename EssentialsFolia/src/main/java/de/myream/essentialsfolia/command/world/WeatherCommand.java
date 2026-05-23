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

        World world;
        if (args.length >= 3) {
            world = Bukkit.getWorld(args[2]);
            if (world == null) { send(sender, "player-not-found", args[2]); return; }
        } else if (sender instanceof Player p) {
            world = p.getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }

        int duration = 600; // 30 seconds in ticks
        if (args.length >= 2) {
            try {
                duration = Integer.parseInt(args[1]) * 20; // seconds to ticks
            } catch (NumberFormatException ignored) {}
        }

        String weatherType = args[0].toLowerCase();
        switch (weatherType) {
            case "clear", "sun", "sunny" -> {
                world.setStorm(false);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                send(sender, "weather-set", world.getName(), "clear");
            }
            case "rain", "rainy" -> {
                world.setStorm(true);
                world.setThundering(false);
                world.setWeatherDuration(duration);
                send(sender, "weather-set", world.getName(), "rain");
            }
            case "thunder", "storm" -> {
                world.setStorm(true);
                world.setThundering(true);
                world.setWeatherDuration(duration);
                world.setThunderDuration(duration);
                send(sender, "weather-set", world.getName(), "thunder");
            }
            default -> sendRaw(sender, "invalid-usage", "/weather <clear|rain|thunder>");
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return List.of("clear", "rain", "thunder");
        return List.of();
    }
}
