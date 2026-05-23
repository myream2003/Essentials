package de.myream.essentialsfolia.command.warp;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand extends AbstractCommand {

    public DelWarpCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.delwarp")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/delwarp <name>");
            return;
        }

        String name = args[0];
        if (!plugin.getWarpManager().deleteWarp(name)) {
            send(sender, "warp-not-found", name);
            return;
        }
        send(sender, "warp-deleted", name);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(plugin.getWarpManager().getWarpNames());
        return List.of();
    }
}
