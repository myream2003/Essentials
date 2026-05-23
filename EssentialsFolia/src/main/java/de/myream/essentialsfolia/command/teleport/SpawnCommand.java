package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends AbstractCommand {

    public SpawnCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.spawn")) return;

        Player self = asPlayer(sender);
        Location spawn = plugin.getSpawnManager().getSpawn();

        if (spawn == null) {
            self.sendMessage(Colors.parse("&cKein Spawn gesetzt."));
            return;
        }

        EssentialsUser user = plugin.getUserManager().get(self);
        if (plugin.getConfig().getBoolean("teleport.save-on-spawn", true)) {
            user.setLastLocation(self.getLocation());
        }

        plugin.getTeleportManager().teleport(self, spawn,
                () -> self.sendMessage(Colors.parse(plugin.msg("spawn-teleported"))));
    }
}
