package de.myream.essentialsfolia.command.moderation;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BanCommand extends AbstractCommand {

    public BanCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.ban")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/ban <player> [reason]");
            return;
        }

        String targetName = args[0];
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "Du wurdest gebannt.";

        BanList<String> banList = Bukkit.getBanList(BanList.Type.NAME);
        if (banList.isBanned(targetName)) {
            send(sender, "ban-already", targetName);
            return;
        }

        banList.addBan(targetName, reason, null, sender.getName());

        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            target.kick(Colors.parse(plugin.msgRaw("ban-message", reason)));
        }

        send(sender, "ban-banned", targetName, reason);
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
