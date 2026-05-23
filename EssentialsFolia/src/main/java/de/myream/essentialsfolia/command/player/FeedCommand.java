package de.myream.essentialsfolia.command.player;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FeedCommand extends AbstractCommand {

    public FeedCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.feed")) return;

        Player target;
        if (args.length > 0) {
            if (!sender.hasPermission("essentials.feed.others")) {
                sendRaw(sender, "no-permission");
                return;
            }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
        }

        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.setExhaustion(0f);

        if (target.equals(sender)) {
            send(sender, "feed-self");
        } else {
            send(sender, "feed-other", target.getName());
            target.sendMessage(Colors.parse(plugin.msg("feed-received", sender.getName())));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("essentials.feed.others")) {
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        }
        return names;
    }
}
