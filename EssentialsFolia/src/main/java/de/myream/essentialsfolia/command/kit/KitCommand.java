package de.myream.essentialsfolia.command.kit;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.model.Kit;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class KitCommand extends AbstractCommand {

    public KitCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        // /kits - list all kits
        if (label.equalsIgnoreCase("kits") || (args.length == 0)) {
            if (!requirePermission(sender, "essentials.kits")) return;
            listKits(sender);
            return;
        }

        if (!requirePermission(sender, "essentials.kit")) return;

        String kitName = args[0];
        Player target;

        if (args.length > 1) {
            if (!sender.hasPermission("essentials.kit.others")) {
                sendRaw(sender, "no-permission");
                return;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) { send(sender, "player-not-found", args[1]); return; }
        } else {
            if (!requirePlayer(sender)) return;
            target = asPlayer(sender);
        }

        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            send(sender, "kit-not-found", kitName);
            return;
        }

        if (!kit.getPermission().isEmpty() && !sender.hasPermission(kit.getPermission())) {
            send(sender, "kit-no-permission", kitName);
            return;
        }

        EssentialsUser user = plugin.getUserManager().get(target);
        long now = System.currentTimeMillis();
        long cooldownExpiry = user.getKitCooldown(kitName);

        if (kit.getCooldown() > 0 && now < cooldownExpiry) {
            long remaining = (cooldownExpiry - now) / 1000;
            send(sender, "kit-cooldown", formatTime(remaining));
            return;
        }

        // Give items
        for (var item : kit.getItems()) {
            target.getInventory().addItem(item.clone());
        }

        if (kit.getCooldown() > 0) {
            user.setKitCooldown(kitName, now + kit.getCooldown() * 1000L);
            plugin.getUserManager().save(user);
        }

        if (target.equals(sender instanceof Player p ? p : null)) {
            send(sender, "kit-received", kitName);
        } else {
            target.sendMessage(Colors.parse(plugin.msg("kit-received", kitName)));
            send(sender, "kit-received-other", target.getName(), kitName);
        }
    }

    private void listKits(CommandSender sender) {
        Collection<String> names = plugin.getKitManager().getKitNames();
        if (names.isEmpty()) {
            send(sender, "kit-none");
            return;
        }
        StringJoiner joiner = new StringJoiner(", ");
        names.forEach(joiner::add);
        send(sender, "kit-list", joiner.toString());
    }

    private String formatTime(long seconds) {
        if (seconds < 60) return seconds + "s";
        if (seconds < 3600) return (seconds / 60) + "m " + (seconds % 60) + "s";
        return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(plugin.getKitManager().getKitNames());
        if (args.length == 2 && sender.hasPermission("essentials.kit.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
}
