package de.myream.essentialsfolia.command.home;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCommand extends AbstractCommand {

    public DelHomeCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.delhome")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        String homeName = args.length > 0 ? args[0] : "home";

        if (!user.deleteHome(homeName)) {
            send(sender, "home-not-found", homeName);
            return;
        }

        plugin.getUserManager().save(user);
        send(sender, "home-deleted", homeName);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            EssentialsUser user = plugin.getUserManager().get(player);
            if (user != null) return new ArrayList<>(user.getHomes().keySet());
        }
        return List.of();
    }
}
