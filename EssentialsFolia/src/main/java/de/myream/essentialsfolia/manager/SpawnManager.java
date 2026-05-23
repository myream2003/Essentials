package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final EssentialsFolia plugin;
    private final File spawnFile;
    private Location spawnLocation;

    public SpawnManager(EssentialsFolia plugin) {
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        load();
    }

    private void load() {
        if (!spawnFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);

        String worldName = cfg.getString("spawn.world");
        if (worldName == null) return;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        spawnLocation = new Location(
                world,
                cfg.getDouble("spawn.x"),
                cfg.getDouble("spawn.y"),
                cfg.getDouble("spawn.z"),
                (float) cfg.getDouble("spawn.yaw"),
                (float) cfg.getDouble("spawn.pitch")
        );
    }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        FoliaLib.runAsync(plugin, this::saveSync);
    }

    private void saveSync() {
        YamlConfiguration cfg = new YamlConfiguration();
        if (spawnLocation != null) {
            cfg.set("spawn.world", spawnLocation.getWorld() != null ? spawnLocation.getWorld().getName() : "world");
            cfg.set("spawn.x", spawnLocation.getX());
            cfg.set("spawn.y", spawnLocation.getY());
            cfg.set("spawn.z", spawnLocation.getZ());
            cfg.set("spawn.yaw", spawnLocation.getYaw());
            cfg.set("spawn.pitch", spawnLocation.getPitch());
        }
        try {
            cfg.save(spawnFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save spawn: " + e.getMessage());
        }
    }

    public Location getSpawn() {
        if (spawnLocation != null) return spawnLocation;
        // Fallback to world spawn
        World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        return world != null ? world.getSpawnLocation() : null;
    }

    public boolean hasSpawn() {
        return spawnLocation != null;
    }
}
