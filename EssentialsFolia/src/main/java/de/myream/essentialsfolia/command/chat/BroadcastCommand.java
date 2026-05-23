package de.myream.essentialsfolia.command.chat;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends AbstractCommand {

    public BroadcastCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.broadcast")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/broadcast <message>");
            return;
        }

        String message = String.join(" ", args);
        String formatted = "&4[&cBroadcast&4] &f" + message;
        plugin.getServer().broadcast(Colors.parse(formatted));
    }
}
