package de.myream.essentialsfolia.command.warp;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand extends AbstractCommand {

    public WarpCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.warp")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/warp <name>");
            return;
        }

        Player self = asPlayer(sender);
        String name = args[0];

        if (plugin.getConfig().getBoolean("warps.per-warp-permission", false)
                && !self.hasPermission("essentials.warp." + name.toLowerCase())) {
            send(sender, "warp-no-permission", name);
            return;
        }

        Location warp = plugin.getWarpManager().getWarp(name);
        if (warp == null) {
            send(sender, "warp-not-found", name);
            return;
        }

        EssentialsUser user = plugin.getUserManager().get(self);
        user.setLastLocation(self.getLocation());

        plugin.getTeleportManager().teleport(self, warp,
                () -> self.sendMessage(Colors.parse(plugin.msg("warp-teleported", name))));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(plugin.getWarpManager().getWarpNames());
        return List.of();
    }
}
