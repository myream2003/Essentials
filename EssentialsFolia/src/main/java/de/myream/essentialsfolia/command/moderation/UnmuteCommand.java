package de.myream.essentialsfolia.command.moderation;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UnmuteCommand extends AbstractCommand {

    public UnmuteCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.unmute")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/unmute <player>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        EssentialsUser user = plugin.getUserManager().get(target);
        if (!user.isMuted()) {
            send(sender, "mute-not-muted", target.getName());
            return;
        }

        user.setMuted(false);
        user.setMuteExpiry(0);
        plugin.getUserManager().save(user);
        send(sender, "mute-unmuted", target.getName());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                // get(UUID) may return null if player not in cache — skip safely
                EssentialsUser u = plugin.getUserManager().get(p.getUniqueId());
                if (u != null && u.isMuted()) names.add(p.getName());
            }
        }
        return names;
    }
}
