package de.myream.essentialsfolia.command.chat;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand extends AbstractCommand {

    public ReplyCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.reply")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/reply <message>");
            return;
        }

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        UUID targetUuid = user.getLastMessageTarget();

        if (targetUuid == null) {
            send(sender, "msg-no-reply");
            return;
        }

        Player target = Bukkit.getPlayer(targetUuid);
        if (target == null || !target.isOnline()) {
            send(sender, "player-not-found", "reply target");
            return;
        }

        String message = String.join(" ", args);
        String format = plugin.getConfig().getString("chat.msg-format",
                "&7[&f{sender} &7-> &f{receiver}&7] &f{message}");
        String formatted = format
                .replace("{sender}", self.getDisplayName())
                .replace("{receiver}", target.getDisplayName())
                .replace("{message}", message);

        self.sendMessage(Colors.parse(formatted));
        target.sendMessage(Colors.parse(formatted));

        EssentialsUser targetUser = plugin.getUserManager().get(target);
        targetUser.setLastMessageTarget(self.getUniqueId());
    }
}
