package de.myream.essentialsfolia.command.warp;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends AbstractCommand {

    public SetWarpCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.setwarp")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/setwarp <name>");
            return;
        }

        Player self = asPlayer(sender);
        String name = args[0];
        plugin.getWarpManager().setWarp(name, self.getLocation());
        send(sender, "warp-set", name);
    }
}
