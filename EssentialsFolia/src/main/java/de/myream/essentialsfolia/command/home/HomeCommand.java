package de.myream.essentialsfolia.command.home;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends AbstractCommand {

    public HomeCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.home")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        String homeName = args.length > 0 ? args[0] : "home";

        Location home = user.getHome(homeName);
        if (home == null) {
            send(sender, "home-not-found", homeName);
            return;
        }

        user.setLastLocation(self.getLocation());
        plugin.getTeleportManager().teleport(self, home,
                () -> self.sendMessage(Colors.parse(plugin.msg("home-teleported", homeName))));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            EssentialsUser user = plugin.getUserManager().get(player);
            return new ArrayList<>(user.getHomes().keySet());
        }
        return List.of();
    }
}
