package de.myream.essentialsfolia.command.teleport;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TpahereCommand extends AbstractCommand {

    public TpahereCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.tpahere")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/" + label + " <player>");
            return;
        }

        Player self = asPlayer(sender);
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        plugin.getTeleportManager().sendTpahereRequest(self, target);

        self.sendMessage(Colors.parse(plugin.msg("tp-request-here-sent", target.getName())));
        target.sendMessage(Colors.parse(plugin.msg("tp-request-here-received", self.getName())));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
        }
        return names;
    }
}
