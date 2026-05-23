package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TimeCommand extends AbstractCommand {

    public TimeCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.time")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/time <day|night|noon|midnight|set <ticks>|add <ticks>> [world]");
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

        String timeStr = null;
        switch (args[0].toLowerCase()) {
            case "day" -> { world.setTime(1000); timeStr = "day (1000)"; }
            case "night" -> { world.setTime(13000); timeStr = "night (13000)"; }
            case "noon" -> { world.setTime(6000); timeStr = "noon (6000)"; }
            case "midnight" -> { world.setTime(18000); timeStr = "midnight (18000)"; }
            case "set" -> {
                if (args.length < 2) { sendRaw(sender, "invalid-usage", "/time set <ticks>"); return; }
                try {
                    long ticks = Long.parseLong(args[1]);
                    world.setTime(ticks);
                    timeStr = String.valueOf(ticks);
                } catch (NumberFormatException e) {
                    sendRaw(sender, "invalid-number", args[1]);
                    return;
                }
            }
            case "add" -> {
                if (args.length < 2) { sendRaw(sender, "invalid-usage", "/time add <ticks>"); return; }
                try {
                    long ticks = Long.parseLong(args[1]);
                    world.setTime(world.getTime() + ticks);
                    timeStr = "+" + ticks;
                } catch (NumberFormatException e) {
                    sendRaw(sender, "invalid-number", args[1]);
                    return;
                }
            }
            default -> {
                sendRaw(sender, "invalid-usage", "/time <day|night|noon|midnight|set|add> ...");
                return;
            }
        }

        send(sender, "time-set", world.getName(), timeStr);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return List.of("day", "night", "noon", "midnight", "set", "add");
        return List.of();
    }
}
