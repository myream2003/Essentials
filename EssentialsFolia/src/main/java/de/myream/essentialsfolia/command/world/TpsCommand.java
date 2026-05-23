package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.command.CommandSender;

public class TpsCommand extends AbstractCommand {

    public TpsCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.tps")) return;

        double[] tps = plugin.getServer().getTPS();
        String t1  = formatTps(tps.length > 0 ? tps[0] : 20.0);
        String t5  = formatTps(tps.length > 1 ? tps[1] : 20.0);
        String t15 = formatTps(tps.length > 2 ? tps[2] : 20.0);

        sender.sendMessage(Colors.parse(plugin.msgRaw("tps-format", t1, t5, t15)));
    }

    private String formatTps(double tps) {
        double clamped = Math.min(tps, 20.0);
        String color = clamped >= 18.0 ? "&a" : clamped >= 15.0 ? "&e" : "&c";
        return color + String.format("%.2f", clamped);
    }
}
