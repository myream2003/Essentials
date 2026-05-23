package de.myream.essentialsfolia.command.player;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.speed")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/speed <0-10> [player]");
            return;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            sendRaw(sender, "invalid-number", args[0]);
            return;
        }

        if (speed < 0 || speed > 10) {
            send(sender, "speed-invalid");
            return;
        }

        Player target;
        boolean forOther = false;
        if (args.length > 1) {
            if (!sender.hasPermission("essentials.speed.others")) {
                sendRaw(sender, "no-permission");
                return;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { send(sender, "player-not-found", args[1]); return; }
            forOther = true;
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
        }

        // Normalize 0-10 to Bukkit's 0-1 range
        float normalized = speed / 10f;
        if (target.isFlying()) {
            target.setFlySpeed(normalized);
        } else {
            target.setWalkSpeed(normalized);
        }

        if (!forOther) {
            send(sender, "speed-set-self", speed);
        } else {
            send(sender, "speed-set-other", target.getName(), speed);
            target.sendMessage(Colors.parse(plugin.msg("speed-set-received", sender.getName(), speed)));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return List.of("1", "2", "3", "5", "10");
        if (args.length == 2 && sender.hasPermission("essentials.speed.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
}
