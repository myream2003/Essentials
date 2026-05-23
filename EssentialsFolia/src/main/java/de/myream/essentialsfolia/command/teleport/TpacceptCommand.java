package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.manager.TeleportManager;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import de.myream.essentialsfolia.util.FoliaLib;
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

        // Check for TPA request (requester -> self)
        UUID tpaRequester = tm.getPendingTpaFor(self.getUniqueId());
        if (tpaRequester != null) {
            Player requester = Bukkit.getPlayer(tpaRequester);
            tm.clearTpaRequest(tpaRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-accepted")));
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-accepted-sender", self.getName())));
                EssentialsUser reqUser = plugin.getUserManager().get(requester);
                reqUser.setLastLocation(requester.getLocation());
                plugin.getTeleportManager().teleport(requester, self.getLocation(), null);
            }
            return;
        }

        // Check for TPAHere request (requester wants self to teleport to them)
        UUID tpahereRequester = tm.getPendingTpahereFor(self.getUniqueId());
        if (tpahereRequester != null) {
            Player requester = Bukkit.getPlayer(tpahereRequester);
            tm.clearTpahereRequest(tpahereRequester);
            self.sendMessage(Colors.parse(plugin.msg("tp-request-accepted")));
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(Colors.parse(plugin.msg("tp-request-accepted-sender", self.getName())));
                EssentialsUser selfUser = plugin.getUserManager().get(self);
                selfUser.setLastLocation(self.getLocation());
                plugin.getTeleportManager().teleport(self, requester.getLocation(), null);
            }
            return;
        }

        send(sender, "tp-no-request");
    }
}
