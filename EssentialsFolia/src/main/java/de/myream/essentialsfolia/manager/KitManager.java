package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.Kit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    private final EssentialsFolia plugin;
    private final Map<String, Kit> kits = new LinkedHashMap<>();

    public KitManager(EssentialsFolia plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        kits.clear();
        ConfigurationSection kitsSection = plugin.getConfig().getConfigurationSection("kits");
        if (kitsSection == null) return;

        for (String key : kitsSection.getKeys(false)) {
            ConfigurationSection kitSection = kitsSection.getConfigurationSection(key);
            if (kitSection == null) continue;

            long cooldown = kitSection.getLong("cooldown", 0);
            String permission = kitSection.getString("permission", "essentials.kit." + key);
            List<String> itemStrings = kitSection.getStringList("items");

            kits.put(key.toLowerCase(), new Kit(key, cooldown, permission, itemStrings));
        }

        plugin.getLogger().info("Loaded " + kits.size() + " kit(s).");
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public boolean exists(String name) {
        return kits.containsKey(name.toLowerCase());
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }

    public Collection<String> getKitNames() {
        return kits.keySet();
    }
}
