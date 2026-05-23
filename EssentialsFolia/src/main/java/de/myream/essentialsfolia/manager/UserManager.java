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

    /** Always returns a non-null user for an online player. */
    public EssentialsUser getOrCreate(Player player) {
        EssentialsUser user = users.computeIfAbsent(player.getUniqueId(),
                uuid -> loadOrCreate(uuid, player.getName()));
        user.setName(player.getName());
        return user;
    }

    /** May return null for players not currently loaded. Use getOrCreate for online players. */
    public EssentialsUser get(UUID uuid) {
        return users.get(uuid);
    }

    /** Always returns a non-null user for an online player. */
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

        if (cfg.isConfigurationSection("homes")) {
            for (String key : cfg.getConfigurationSection("homes").getKeys(false)) {
                Location loc = deserializeLocation(cfg, "homes." + key);
                if (loc != null) user.setHome(key, loc);
            }
        }

        Location lastLoc = deserializeLocation(cfg, "lastLocation");
        if (lastLoc != null) user.setLastLocation(lastLoc);

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

        synchronized (user) {
            for (Map.Entry<String, Location> entry : user.getHomes().entrySet()) {
                serializeLocation(cfg, "homes." + entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Long> entry : user.getKitCooldowns().entrySet()) {
                cfg.set("kitCooldowns." + entry.getKey(), entry.getValue());
            }
        }

        if (user.getLastLocation() != null) {
            serializeLocation(cfg, "lastLocation", user.getLastLocation());
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + user.getName() + ": " + e.getMessage());
        }
    }

    /** Saves all loaded users asynchronously. */
    public void saveAll() {
        for (EssentialsUser user : users.values()) {
            FoliaLib.runAsync(plugin, () -> saveSync(user));
        }
    }

    /** Saves user data and removes them from the in-memory cache. */
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
        return new Location(
                world,
                cfg.getDouble(path + ".x"),
                cfg.getDouble(path + ".y"),
                cfg.getDouble(path + ".z"),
                (float) cfg.getDouble(path + ".yaw"),
                (float) cfg.getDouble(path + ".pitch")
        );
    }
}
