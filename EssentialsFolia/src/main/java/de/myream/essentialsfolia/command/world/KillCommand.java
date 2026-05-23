package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillCommand extends AbstractCommand {

    public KillCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.kill")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/kill <player>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        if (target.equals(sender)) {
            send(sender, "kill-self");
            return;
        }

        target.setHealth(0);
        send(sender, "kill-killed", target.getName());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        }
        return names;
    }
}
