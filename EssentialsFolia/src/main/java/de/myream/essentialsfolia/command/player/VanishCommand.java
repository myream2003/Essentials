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

public class VanishCommand extends AbstractCommand {

    public VanishCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.vanish")) return;

        Player target;
        boolean forOther = false;

        if (args.length > 0 && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
            if (!sender.hasPermission("essentials.vanish.others")) {
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

        EssentialsUser user = plugin.getUserManager().get(target);
        boolean current = user.isVanished();
        boolean newVanish;

        if (args.length > 0 && (args[args.length - 1].equalsIgnoreCase("on") || args[args.length - 1].equalsIgnoreCase("off"))) {
            newVanish = args[args.length - 1].equalsIgnoreCase("on");
        } else {
            newVanish = !current;
        }

        user.setVanished(newVanish);

        final Player finalTarget = target;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(finalTarget)) continue;
            if (newVanish && !online.hasPermission("essentials.vanish.see")) {
                online.hidePlayer(plugin, finalTarget);
            } else {
                online.showPlayer(plugin, finalTarget);
            }
        }

        if (!forOther) {
            send(sender, newVanish ? "vanish-enabled-self" : "vanish-disabled-self");
        } else {
            send(sender, newVanish ? "vanish-enabled-other" : "vanish-disabled-other", target.getName());
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>(List.of("on", "off"));
            if (sender.hasPermission("essentials.vanish.others")) {
                for (Player p : Bukkit.getOnlinePlayers()) result.add(p.getName());
            }
            return result;
        }
        return List.of();
    }
}
