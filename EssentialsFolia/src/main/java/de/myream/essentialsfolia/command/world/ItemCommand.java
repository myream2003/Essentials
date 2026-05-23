package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemCommand extends AbstractCommand {

    public ItemCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.item")) return;

        if (args.length == 0) {
            sendRaw(sender, "invalid-usage", "/item <item> [amount]");
            return;
        }

        Player self = asPlayer(sender);
        Material material = Material.matchMaterial(args[0].toUpperCase());
        if (material == null || material == Material.AIR) {
            send(sender, "item-invalid", args[0]);
            return;
        }

        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 1) amount = 1;
                if (amount > material.getMaxStackSize()) amount = material.getMaxStackSize();
            } catch (NumberFormatException e) {
                sendRaw(sender, "invalid-number", args[1]);
                return;
            }
        }

        self.getInventory().addItem(new ItemStack(material, amount));
        send(sender, "item-given", amount, material.name().toLowerCase());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            List<String> mats = new ArrayList<>();
            String query = args[0].toUpperCase();
            for (Material m : Material.values()) {
                if (m.isItem() && m.name().startsWith(query)) mats.add(m.name().toLowerCase());
                if (mats.size() > 50) break;
            }
            return mats;
        }
        return List.of();
    }
}
