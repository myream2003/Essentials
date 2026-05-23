package de.myream.essentialsfolia.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final String name;
    private final long cooldown;
    private final String permission;
    private final List<ItemStack> items;

    public Kit(String name, long cooldown, String permission, List<String> itemStrings) {
        this.name = name;
        this.cooldown = cooldown;
        this.permission = permission;
        this.items = parseItems(itemStrings);
    }

    private List<ItemStack> parseItems(List<String> itemStrings) {
        List<ItemStack> result = new ArrayList<>();
        for (String entry : itemStrings) {
            if (entry == null || entry.isBlank()) continue;
            String[] parts = entry.split(":");

            Material material = Material.matchMaterial(parts[0].toUpperCase());
            if (material == null || !material.isItem()) continue;

            int amount = parts.length > 1 ? safeInt(parts[1], 1) : 1;
            amount = Math.max(1, Math.min(amount, material.getMaxStackSize()));
            ItemStack item = new ItemStack(material, amount);

            if (parts.length > 2 && !parts[2].isBlank()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(parts[2].replace("_", " ").replace('&', '§'));
                    item.setItemMeta(meta);
                }
            }

            result.add(item);
        }
        return result;
    }

    private int safeInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public String getName()        { return name; }
    public long getCooldown()      { return cooldown; }
    public String getPermission()  { return permission; }
    public List<ItemStack> getItems() { return new ArrayList<>(items); }
}
