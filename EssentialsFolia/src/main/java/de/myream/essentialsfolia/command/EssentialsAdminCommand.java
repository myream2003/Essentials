package de.myream.essentialsfolia.command;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EssentialsAdminCommand extends AbstractCommand {

    public EssentialsAdminCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.admin")) return;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            send(sender, "reloaded");
            return;
        }

        sender.sendMessage(Colors.parse("&aEssentialsFolia &ev" + plugin.getDescription().getVersion()));
        sender.sendMessage(Colors.parse("&7/essentials reload &8- &fKonfiguration neu laden"));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return List.of("reload");
        return List.of();
    }
}
