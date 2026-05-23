package de.myream.essentialsfolia.command.home;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.StringJoiner;

public class HomesCommand extends AbstractCommand {

    public HomesCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.homes")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        Set<String> homeNames = user.getHomes().keySet();

        if (homeNames.isEmpty()) {
            send(sender, "home-none");
            return;
        }

        StringJoiner joiner = new StringJoiner(", ");
        homeNames.forEach(joiner::add);
        send(sender, "home-list", joiner.toString());
    }
}
