package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.manager.TeleportManager;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpdenyCommand extends AbstractCommand {

    public TpdenyCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.tpdeny")) return;

        Player self = asPlayer(sender);
        TeleportManager tm = plugin.getTeleportManager();

        UUID tpaRequester = tm.getPendingTpaFor(self.getUniqueId());
        if (tpaRequester != null) {
            Player requester = Bukkit.getPlayer(tpaRequester);
            tm.clearTpaRequest(tpaRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-denied")));
            if (requester != null) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-denied-sender", self.getName())));
            }
            return;
        }

        UUID tpahereRequester = tm.getPendingTpahereFor(self.getUniqueId());
        if (tpahereRequester != null) {
            Player requester = Bukkit.getPlayer(tpahereRequester);
            tm.clearTpahereRequest(tpahereRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-denied")));
            if (requester != null) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-denied-sender", self.getName())));
            }
            return;
        }

        send(sender, "tp-no-request");
    }
}
