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
        boolean forOther;

        if (args.length > 0 && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
            if (!sender.hasPermission("essentials.fly.others")) { sendRaw(sender, "no-permission"); return; }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
            forOther = true;
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
            forOther = false;
        }

        EssentialsUser user = plugin.getUserManager().get(target);
        boolean newFly = determineToggle(args, forOther ? 1 : 0, !target.getAllowFlight());

        target.setAllowFlight(newFly);
        if (!newFly) target.setFlying(false);

        user.setFlyMode(newFly);
        plugin.getUserManager().save(user);

        if (forOther) {
            send(sender, newFly ? "fly-enabled-other" : "fly-disabled-other", target.getName());
            target.sendMessage(Colors.parse(plugin.msg(
                    newFly ? "fly-enabled-received" : "fly-disabled-received", sender.getName())));
        } else {
            send(sender, newFly ? "fly-enabled-self" : "fly-disabled-self");
        }
    }

    private boolean determineToggle(String[] args, int idx, boolean defaultToggle) {
        if (args.length > idx) {
            if (args[idx].equalsIgnoreCase("on")) return true;
            if (args[idx].equalsIgnoreCase("off")) return false;
        }
        return defaultToggle;
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
