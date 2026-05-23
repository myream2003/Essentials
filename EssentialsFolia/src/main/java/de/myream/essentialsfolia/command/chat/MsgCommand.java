package de.myream.essentialsfolia.command.chat;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MsgCommand extends AbstractCommand {

    public MsgCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.msg")) return;

        if (args.length < 2) {
            sendRaw(sender, "invalid-usage", "/msg <player> <message>");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String senderName = sender instanceof Player p ? p.getDisplayName() : sender.getName();

        String format = plugin.getConfig().getString("chat.msg-format",
                "&7[&f{sender} &7-> &f{receiver}&7] &f{message}");
        String formatted = format
                .replace("{sender}", senderName)
                .replace("{receiver}", target.getDisplayName())
                .replace("{message}", message);

        sender.sendMessage(Colors.parse(formatted));
        target.sendMessage(Colors.parse(formatted));

        // Update reply targets
        if (sender instanceof Player sp) {
            EssentialsUser senderUser = plugin.getUserManager().get(sp);
            senderUser.setLastMessageTarget(target.getUniqueId());
        }
        EssentialsUser targetUser = plugin.getUserManager().get(target);
        if (sender instanceof Player sp) {
            targetUser.setLastMessageTarget(sp.getUniqueId());
        }

        // Notify if AFK
        EssentialsUser tu = plugin.getUserManager().get(target);
        if (tu != null && tu.isAfk()) {
            String afkMsg = plugin.msgRaw("msg-player-afk", target.getDisplayName(), tu.getAfkMessage());
            sender.sendMessage(Colors.parse(afkMsg));
        }

        // Social spy
        if (plugin.getConfig().getBoolean("chat.log-private-messages", true)) {
            plugin.getLogger().info("[MSG] " + senderName + " -> " + target.getName() + ": " + message);
        }
        if (plugin.getConfig().getBoolean("chat.socialspy", true)) {
            for (Player spy : Bukkit.getOnlinePlayers()) {
                if (spy.equals(sender) || spy.equals(target)) continue;
                if (spy.hasPermission("essentials.socialspy")) {
                    spy.sendMessage(Colors.parse("&8[SocialSpy] " + formatted));
                }
            }
        }
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
