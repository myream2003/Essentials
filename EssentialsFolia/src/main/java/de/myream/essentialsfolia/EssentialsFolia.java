package de.myream.essentialsfolia;

import de.myream.essentialsfolia.command.CommandManager;
import de.myream.essentialsfolia.listener.PlayerJoinQuitListener;
import de.myream.essentialsfolia.listener.PlayerMoveListener;
import de.myream.essentialsfolia.listener.ChatListener;
import de.myream.essentialsfolia.listener.DamageListener;
import de.myream.essentialsfolia.manager.*;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class EssentialsFolia extends JavaPlugin {

    private static EssentialsFolia instance;

    private UserManager userManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private TeleportManager teleportManager;
    private KitManager kitManager;

    private YamlConfiguration messages;
    private final Instant startTime = Instant.now();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadMessages();

        userManager = new UserManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        teleportManager = new TeleportManager(this);
        kitManager = new KitManager(this);

        new CommandManager(this).register();

        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        // AFK auto-detection every 10 seconds
        int autoAfk = getConfig().getInt("afk.auto-afk", 300);
        if (autoAfk > 0) {
            FoliaLib.runAsyncRepeating(this,
                    () -> getServer().getOnlinePlayers().forEach(p -> {
                        var user = userManager.get(p);
                        if (user == null) return;
                        // Movement-based AFK is tracked in PlayerMoveListener
                    }),
                    10, 10, java.util.concurrent.TimeUnit.SECONDS);
        }

        getLogger().info("EssentialsFolia v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (userManager != null) userManager.saveAll();
        getLogger().info("EssentialsFolia disabled.");
    }

    public void reload() {
        reloadConfig();
        loadMessages();
        kitManager.load();
    }

    private void loadMessages() {
        File msgFile = new File(getDataFolder(), "messages.yml");
        if (!msgFile.exists()) saveResource("messages.yml", false);

        messages = YamlConfiguration.loadConfiguration(msgFile);

        // Merge defaults from jar
        InputStream defaultStream = getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaults);
        }
    }

    public String msg(String key, Object... args) {
        String prefix = getConfig().getString("general.prefix", "&8[&aEssentials&8]&r ");
        String raw = messages.getString(key, "&c[Missing message: " + key + "]");
        for (int i = 0; i < args.length; i++) {
            raw = raw.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return prefix + raw;
    }

    public String msgRaw(String key, Object... args) {
        String raw = messages.getString(key, "&c[Missing: " + key + "]");
        for (int i = 0; i < args.length; i++) {
            raw = raw.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return raw;
    }

    public static EssentialsFolia getInstance() { return instance; }
    public UserManager getUserManager() { return userManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public KitManager getKitManager() { return kitManager; }
    public YamlConfiguration getMessages() { return messages; }
    public Instant getStartTime() { return startTime; }
}
