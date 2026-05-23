package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpCommand extends AbstractCommand {

    public TpCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.tp")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/tp <player> | /tp <x> <y> <z> [world] | /tp <player1> <player2>");
            return;
        }

        if (args.length == 1) {
            // /tp <player> — teleport self to player
            if (!requirePlayer(sender)) return;
            Player self = asPlayer(sender);
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { send(sender, "player-not-found", args[0]); return; }
            plugin.getUserManager().get(self).setLastLocation(self.getLocation());
            plugin.getTeleportManager().teleport(self, target.getLocation(),
                    () -> self.sendMessage(Colors.parse(plugin.msg("tp-teleported", target.getName()))));
            return;
        }

        if (args.length == 2) {
            // /tp <player1> <player2> — teleport player1 to player2
            Player from = Bukkit.getPlayerExact(args[0]);
            Player to = Bukkit.getPlayerExact(args[1]);
            if (from == null) { send(sender, "player-not-found", args[0]); return; }
            if (to == null) { send(sender, "player-not-found", args[1]); return; }
            FoliaLib.teleport(plugin, from, to.getLocation(),
                    () -> from.sendMessage(Colors.parse(plugin.msg("tp-teleported", to.getName()))));
            send(sender, "tp-teleported", from.getName() + " -> " + to.getName());
            return;
        }

        // /tp <x> <y> <z> [world]
        if (!requirePlayer(sender)) return;
        Player self = asPlayer(sender);
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            World world = args.length > 3 ? Bukkit.getWorld(args[3]) : self.getWorld();
            if (world == null) {
                send(sender, "player-not-found", args[3]);
                return;
            }
            Location dest = new Location(world, x, y, z, self.getYaw(), self.getPitch());
            plugin.getUserManager().get(self).setLastLocation(self.getLocation());
            plugin.getTeleportManager().teleport(self, dest,
                    () -> self.sendMessage(Colors.parse(plugin.msg("tp-teleported-coords",
                            String.format("%.1f", x), String.format("%.1f", y), String.format("%.1f", z),
                            world.getName()))));
        } catch (NumberFormatException e) {
            sendRaw(sender, "invalid-usage", "/tp <x> <y> <z> [world]");
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1 || args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        }
        return names;
    }
}
