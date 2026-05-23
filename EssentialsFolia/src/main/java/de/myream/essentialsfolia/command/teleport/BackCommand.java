package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand extends AbstractCommand {

    public BackCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.back")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        Location last = user.getLastLocation();

        if (last == null) {
            send(sender, "back-no-location");
            return;
        }

        Location current = self.getLocation();
        user.setLastLocation(current);

        plugin.getTeleportManager().teleport(self, last,
                () -> self.sendMessage(Colors.parse(plugin.msg("back-teleported"))));
    }
}
