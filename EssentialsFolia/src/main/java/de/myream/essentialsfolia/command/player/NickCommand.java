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

public class NickCommand extends AbstractCommand {

    public NickCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.nick")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/nick <nickname|off> [player]");
            return;
        }

        Player target;
        String nickArg;

        if (args.length >= 2 && sender.hasPermission("essentials.nick.others")) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
            nickArg = args[1];
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
            nickArg = args[0];
        }

        boolean forOther = !target.equals(sender instanceof Player ? (Player) sender : null);

        if (nickArg.equalsIgnoreCase("off") || nickArg.equalsIgnoreCase("reset")) {
            EssentialsUser user = plugin.getUserManager().get(target);
            user.setNickname(null);
            target.setDisplayName(target.getName());
            plugin.getUserManager().save(user);
            if (!forOther) send(sender, "nick-cleared-self");
            else send(sender, "nick-cleared-other", target.getName());
            return;
        }

        // Strip colors if no permission
        String nick = nickArg;
        if (!sender.hasPermission("essentials.nick.color")) {
            nick = Colors.stripColor(nick);
        }

        // Check length
        int maxLen = plugin.getConfig().getInt("nick.max-length", 16);
        if (maxLen > 0 && Colors.stripColor(nick).length() > maxLen) {
            send(sender, "nick-too-long", maxLen);
            return;
        }

        String prefix = plugin.getConfig().getString("nick.prefix", "~");
        String displayNick = prefix + nick;

        EssentialsUser user = plugin.getUserManager().get(target);
        user.setNickname(displayNick);
        target.setDisplayName(Colors.colorize(displayNick));
        plugin.getUserManager().save(user);

        if (!forOther) {
            send(sender, "nick-set-self", Colors.colorize(displayNick));
        } else {
            send(sender, "nick-set-other", target.getName(), Colors.colorize(displayNick));
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("essentials.nick.others")) {
            List<String> result = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) result.add(p.getName());
            return result;
        }
        return List.of();
    }
}
