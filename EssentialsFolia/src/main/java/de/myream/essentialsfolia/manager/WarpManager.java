package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {

    private final EssentialsFolia plugin;
    private final Map<String, Location> warps = new ConcurrentHashMap<>();
    private final File warpFile;

    public WarpManager(EssentialsFolia plugin) {
        this.plugin = plugin;
        this.warpFile = new File(plugin.getDataFolder(), "warps.yml");
        load();
    }

    private void load() {
        if (!warpFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(warpFile);
        if (!cfg.isConfigurationSection("warps")) return;

        for (String name : cfg.getConfigurationSection("warps").getKeys(false)) {
            String path = "warps." + name;
            String worldName = cfg.getString(path + ".world");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            warps.put(name.toLowerCase(), new Location(
                    world,
                    cfg.getDouble(path + ".x"),
                    cfg.getDouble(path + ".y"),
                    cfg.getDouble(path + ".z"),
                    (float) cfg.getDouble(path + ".yaw"),
                    (float) cfg.getDouble(path + ".pitch")
            ));
        }
    }

    public void save() {
        // Snapshot the map to avoid race conditions during async serialization
        Map<String, Location> snapshot = new HashMap<>(warps);
        FoliaLib.runAsync(plugin, () -> saveSync(snapshot));
    }

    private void saveSync(Map<String, Location> snapshot) {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Location> entry : snapshot.entrySet()) {
            String path = "warps." + entry.getKey();
            Location loc = entry.getValue();
            cfg.set(path + ".world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            cfg.set(path + ".x", loc.getX());
            cfg.set(path + ".y", loc.getY());
            cfg.set(path + ".z", loc.getZ());
            cfg.set(path + ".yaw", loc.getYaw());
            cfg.set(path + ".pitch", loc.getPitch());
        }
        try {
            cfg.save(warpFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps: " + e.getMessage());
        }
    }

    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public void setWarp(String name, Location location) {
        warps.put(name.toLowerCase(), location.clone());
        save();
    }

    public boolean deleteWarp(String name) {
        boolean removed = warps.remove(name.toLowerCase()) != null;
        if (removed) save();
        return removed;
    }

    public boolean exists(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public Set<String> getWarpNames() {
        return warps.keySet();
    }
}
