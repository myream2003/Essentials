package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.manager.TeleportManager;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpacceptCommand extends AbstractCommand {

    public TpacceptCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.tpaccept")) return;

        Player self = asPlayer(sender);
        TeleportManager tm = plugin.getTeleportManager();

        // Check for TPA: someone requested to teleport to self
        UUID tpaRequester = tm.getPendingTpaFor(self.getUniqueId());
        if (tpaRequester != null) {
            tm.clearTpaRequest(tpaRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-accepted")));

            Player requester = Bukkit.getPlayer(tpaRequester);
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-accepted-sender", self.getName())));
                plugin.getUserManager().get(requester).setLastLocation(requester.getLocation());
                tm.teleport(requester, self.getLocation(), null);
            }
            return;
        }

        // Check for TPAHere: someone wants self to teleport to them
        UUID tpahereRequester = tm.getPendingTpahereFor(self.getUniqueId());
        if (tpahereRequester != null) {
            tm.clearTpahereRequest(tpahereRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-accepted")));

            Player requester = Bukkit.getPlayer(tpahereRequester);
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-accepted-sender", self.getName())));
                plugin.getUserManager().get(self).setLastLocation(self.getLocation());
                tm.teleport(self, requester.getLocation(), null);
            }
            return;
        }

        send(sender, "tp-no-request");
    }
}
