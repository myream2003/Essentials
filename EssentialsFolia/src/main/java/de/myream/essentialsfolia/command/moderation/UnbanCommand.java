package de.myream.essentialsfolia.command.moderation;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UnbanCommand extends AbstractCommand {

    public UnbanCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.unban")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/unban <player>");
            return;
        }

        String targetName = args[0];
        BanList<String> banList = Bukkit.getBanList(BanList.Type.NAME);

        if (!banList.isBanned(targetName)) {
            send(sender, "unban-not-banned", targetName);
            return;
        }

        banList.pardon(targetName);
        send(sender, "unban-unbanned", targetName);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return Bukkit.getBanList(BanList.Type.NAME).getBanEntries()
                    .stream()
                    .map(e -> e.getTarget())
                    .toList();
        }
        return List.of();
    }
}
