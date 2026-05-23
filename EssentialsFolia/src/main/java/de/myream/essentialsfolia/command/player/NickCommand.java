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
        boolean forOther;

        // /nick <player> <nick>
        if (args.length >= 2 && sender.hasPermission("essentials.nick.others")) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
            nickArg = args[1];
            forOther = !(sender instanceof Player sp) || !sp.equals(target);
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
            nickArg = args[0];
            forOther = false;
        }

        EssentialsUser user = plugin.getUserManager().get(target);

        if (nickArg.equalsIgnoreCase("off") || nickArg.equalsIgnoreCase("reset")) {
            user.setNickname(null);
            target.setDisplayName(target.getName());
            plugin.getUserManager().save(user);
            if (forOther) send(sender, "nick-cleared-other", target.getName());
            else send(sender, "nick-cleared-self");
            return;
        }

        String nick = nickArg;
        if (!sender.hasPermission("essentials.nick.color")) {
            nick = Colors.stripColor(nick);
        }

        int maxLen = plugin.getConfig().getInt("nick.max-length", 16);
        if (maxLen > 0 && Colors.stripColor(nick).length() > maxLen) {
            send(sender, "nick-too-long", maxLen);
            return;
        }

        String prefix = plugin.getConfig().getString("nick.prefix", "~");
        String displayNick = prefix + nick;

        user.setNickname(displayNick);
        target.setDisplayName(Colors.colorize(displayNick));
        plugin.getUserManager().save(user);

        if (forOther) {
            send(sender, "nick-set-other", target.getName(), Colors.colorize(displayNick));
        } else {
            send(sender, "nick-set-self", Colors.colorize(displayNick));
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
