package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends AbstractCommand {

    public GiveCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.give")) return;

        if (args.length < 2) {
            sendRaw(sender, "invalid-usage", "/give <player> <item> [amount]");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { send(sender, "player-not-found", args[0]); return; }

        Material material = Material.matchMaterial(args[1].toUpperCase());
        if (material == null || material == Material.AIR) {
            send(sender, "item-invalid", args[1]);
            return;
        }

        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1) amount = 1;
            } catch (NumberFormatException e) {
                sendRaw(sender, "invalid-number", args[2]);
                return;
            }
        }

        ItemStack item = new ItemStack(material, amount);
        target.getInventory().addItem(item);

        send(sender, "give-given", amount, material.name().toLowerCase(), target.getName());
        target.sendMessage(Colors.parse(plugin.msg("give-received", amount, material.name().toLowerCase())));
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        if (args.length == 2) {
            List<String> mats = new ArrayList<>();
            String query = args[1].toUpperCase();
            for (Material m : Material.values()) {
                if (m.isItem() && m.name().startsWith(query)) mats.add(m.name().toLowerCase());
                if (mats.size() > 50) break;
            }
            return mats;
        }
        return List.of();
    }
}
