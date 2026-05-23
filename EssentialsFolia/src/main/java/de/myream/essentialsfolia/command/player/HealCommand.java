package de.myream.essentialsfolia.command.player;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand extends AbstractCommand {

    public HealCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.heal")) return;

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("essentials.heal.others")) {
                sendRaw(sender, "no-permission");
                return;
            }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
        }

        AttributeInstance maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double max = maxHealth != null ? maxHealth.getValue() : 20.0;
        target.setHealth(max);
        target.setFireTicks(0);

        if (target.equals(sender)) {
            send(sender, "heal-self");
        } else {
            send(sender, "heal-other", target.getName());
            target.sendMessage(Colors.parse(plugin.msg("heal-received", sender.getName())));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("essentials.heal.others")) {
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        }
        return names;
    }
}
