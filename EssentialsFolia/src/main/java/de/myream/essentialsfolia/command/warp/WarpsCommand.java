package de.myream.essentialsfolia.command.warp;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.StringJoiner;

public class WarpsCommand extends AbstractCommand {

    public WarpsCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.warps")) return;

        Collection<String> names = plugin.getWarpManager().getWarpNames();
        if (names.isEmpty()) {
            send(sender, "warp-none");
            return;
        }

        StringJoiner joiner = new StringJoiner(", ");
        names.forEach(joiner::add);
        send(sender, "warp-list", joiner.toString());
    }
}
