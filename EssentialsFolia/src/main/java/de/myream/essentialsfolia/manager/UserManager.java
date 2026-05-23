package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final EssentialsFolia plugin;
    private final Map<UUID, EssentialsUser> users = new ConcurrentHashMap<>();
    private final File dataFolder;

    public UserManager(EssentialsFolia plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "players");
        this.dataFolder.mkdirs();
    }

    public EssentialsUser getOrCreate(Player player) {
        return users.computeIfAbsent(player.getUniqueId(),
                uuid -> loadOrCreate(uuid, player.getName()));
    }

    public EssentialsUser get(UUID uuid) {
        return users.get(uuid);
    }

    public EssentialsUser get(Player player) {
        return getOrCreate(player);
    }

    private EssentialsUser loadOrCreate(UUID uuid, String name) {
        EssentialsUser user = new EssentialsUser(uuid, name);
        File file = new File(dataFolder, uuid + ".yml");
        if (!file.exists()) return user;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        user.setName(cfg.getString("name", name));
        user.setGodMode(cfg.getBoolean("god", false));
        user.setFlyMode(cfg.getBoolean("fly", false));
        user.setNickname(cfg.getString("nick", null));
        user.setMuted(cfg.getBoolean("muted", false));
        user.setMuteExpiry(cfg.getLong("muteExpiry", 0));

        // Load homes
        if (cfg.isConfigurationSection("homes")) {
            for (String key : cfg.getConfigurationSection("homes").getKeys(false)) {
                Location loc = deserializeLocation(cfg, "homes." + key);
                if (loc != null) user.setHome(key, loc);
            }
        }

        // Load last location
        Location lastLoc = deserializeLocation(cfg, "lastLocation");
        if (lastLoc != null) user.setLastLocation(lastLoc);

        // Load kit cooldowns
        if (cfg.isConfigurationSection("kitCooldowns")) {
            for (String kit : cfg.getConfigurationSection("kitCooldowns").getKeys(false)) {
                user.setKitCooldown(kit, cfg.getLong("kitCooldowns." + kit));
            }
        }

        return user;
    }

    public void save(EssentialsUser user) {
        FoliaLib.runAsync(plugin, () -> saveSync(user));
    }

    private void saveSync(EssentialsUser user) {
        File file = new File(dataFolder, user.getUuid() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();

        cfg.set("name", user.getName());
        cfg.set("god", user.isGodMode());
        cfg.set("fly", user.isFlyMode());
        cfg.set("nick", user.getNickname());
        cfg.set("muted", user.isMuted());
        cfg.set("muteExpiry", user.getMuteExpiry());

        // Save homes
        for (Map.Entry<String, Location> entry : user.getHomes().entrySet()) {
            serializeLocation(cfg, "homes." + entry.getKey(), entry.getValue());
        }

        // Save last location
        if (user.getLastLocation() != null) {
            serializeLocation(cfg, "lastLocation", user.getLastLocation());
        }

        // Save kit cooldowns
        for (Map.Entry<String, Long> entry : user.getKitCooldowns().entrySet()) {
            cfg.set("kitCooldowns." + entry.getKey(), entry.getValue());
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + user.getName() + ": " + e.getMessage());
        }
    }

    public void saveAll() {
        users.values().forEach(this::saveSync);
    }

    public void unload(UUID uuid) {
        EssentialsUser user = users.remove(uuid);
        if (user != null) saveSync(user);
    }

    private void serializeLocation(YamlConfiguration cfg, String path, Location loc) {
        cfg.set(path + ".world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
    }

    private Location deserializeLocation(YamlConfiguration cfg, String path) {
        if (!cfg.isConfigurationSection(path)) return null;
        String worldName = cfg.getString(path + ".world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = cfg.getDouble(path + ".x");
        double y = cfg.getDouble(path + ".y");
        double z = cfg.getDouble(path + ".z");
        float yaw = (float) cfg.getDouble(path + ".yaw");
        float pitch = (float) cfg.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
