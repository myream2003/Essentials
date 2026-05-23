package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.Instant;

public class UptimeCommand extends AbstractCommand {

    public UptimeCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.uptime")) return;

        Duration uptime = Duration.between(plugin.getStartTime(), Instant.now());
        long days = uptime.toDays();
        long hours = uptime.toHoursPart();
        long minutes = uptime.toMinutesPart();
        long seconds = uptime.toSecondsPart();

        String formatted = days > 0
                ? days + "d " + hours + "h " + minutes + "m " + seconds + "s"
                : hours > 0
                ? hours + "h " + minutes + "m " + seconds + "s"
                : minutes > 0
                ? minutes + "m " + seconds + "s"
                : seconds + "s";

        send(sender, "uptime-format", formatted);
    }
}
