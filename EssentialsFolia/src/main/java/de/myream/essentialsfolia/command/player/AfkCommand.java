package de.myream.essentialsfolia.command.player;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand extends AbstractCommand {

    public AfkCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.afk")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);

        boolean nowAfk = !user.isAfk();
        user.setAfk(nowAfk);
        user.setAfkMessage(args.length > 0 ? String.join(" ", args) : "");

        String broadcastKey = nowAfk ? "afk-self" : "afk-back";
        plugin.getServer().broadcast(Colors.parse(plugin.msgRaw(broadcastKey, self.getDisplayName())));
    }
}
