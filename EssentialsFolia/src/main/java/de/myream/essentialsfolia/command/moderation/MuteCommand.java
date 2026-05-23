package de.myream.essentialsfolia.command.moderation;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MuteCommand extends AbstractCommand {

    public MuteCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.mute")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/mute <player> [duration in seconds]");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        EssentialsUser user = plugin.getUserManager().get(target);

        long expiryTime = 0;
        if (args.length > 1) {
            try {
                long seconds = Long.parseLong(args[1]);
                if (seconds > 0) expiryTime = System.currentTimeMillis() + seconds * 1000L;
            } catch (NumberFormatException e) {
                sendRaw(sender, "invalid-number", args[1]);
                return;
            }
        }

        user.setMuted(true);
        user.setMuteExpiry(expiryTime);
        plugin.getUserManager().save(user);
        send(sender, "mute-muted", target.getName());
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
