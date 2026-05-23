package de.myream.essentialsfolia.command.player;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand extends AbstractCommand {

    public FlyCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.fly")) return;

        Player target;
        boolean forOther = false;

        if (args.length > 0 && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
            if (!sender.hasPermission("essentials.fly.others")) {
                sendRaw(sender, "no-permission");
                return;
            }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
            forOther = true;
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
        }

        boolean currentFly = target.getAllowFlight();
        boolean newFly;

        if (args.length > 0 && (args[args.length - 1].equalsIgnoreCase("on") || args[args.length - 1].equalsIgnoreCase("off"))) {
            newFly = args[args.length - 1].equalsIgnoreCase("on");
        } else {
            newFly = !currentFly;
        }

        target.setAllowFlight(newFly);
        target.setFlying(newFly);

        EssentialsUser user = plugin.getUserManager().get(target);
        user.setFlyMode(newFly);
        plugin.getUserManager().save(user);

        if (!forOther) {
            send(sender, newFly ? "fly-enabled-self" : "fly-disabled-self");
        } else {
            send(sender, newFly ? "fly-enabled-other" : "fly-disabled-other", target.getName());
            target.sendMessage(Colors.parse(plugin.msg(newFly ? "fly-enabled-received" : "fly-disabled-received", sender.getName())));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>(List.of("on", "off"));
            if (sender.hasPermission("essentials.fly.others")) {
                for (Player p : Bukkit.getOnlinePlayers()) result.add(p.getName());
            }
            return result;
        }
        if (args.length == 2) return List.of("on", "off");
        return List.of();
    }
}
