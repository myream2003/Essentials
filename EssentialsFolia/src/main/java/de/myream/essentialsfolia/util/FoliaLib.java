package de.myream.essentialsfolia.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Folia-compatible scheduling utilities.
 * Wraps the regionalized scheduler API to avoid boilerplate throughout the codebase.
 */
public final class FoliaLib {

    private FoliaLib() {}

    /** Run a task on the region thread that owns the given location. */
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        Bukkit.getRegionScheduler().run(plugin, location, ignored -> task.run());
    }

    /** Run a task on the region thread that owns the given location after a delay. */
    public static void runAtLocationLater(Plugin plugin, Location location, Runnable task, long delayTicks) {
        Bukkit.getRegionScheduler().runDelayed(plugin, location, ignored -> task.run(), delayTicks);
    }

    /** Run a task on the entity's owning region thread. */
    public static void runForEntity(Plugin plugin, Entity entity, Runnable task) {
        entity.getScheduler().run(plugin, ignored -> task.run(), null);
    }

    /** Run a task on the entity's owning region thread after a delay. */
    public static void runForEntityLater(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        entity.getScheduler().runDelayed(plugin, ignored -> task.run(), null, delayTicks);
    }

    /** Run a task asynchronously. */
    public static void runAsync(Plugin plugin, Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, ignored -> task.run());
    }

    /** Run a task asynchronously after a delay. */
    public static void runAsyncLater(Plugin plugin, Runnable task, long delay, TimeUnit unit) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, ignored -> task.run(), delay, unit);
    }

    /** Run a repeating async task. */
    public static void runAsyncRepeating(Plugin plugin, Runnable task, long initialDelay, long period, TimeUnit unit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, ignored -> task.run(), initialDelay, period, unit);
    }

    /** Run a task on the global region thread (server-wide state). */
    public static void runGlobal(Plugin plugin, Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, ignored -> task.run());
    }

    /** Run a repeating task on the global region thread. */
    public static void runGlobalRepeating(Plugin plugin, Runnable task, long initialDelayTicks, long periodTicks) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, ignored -> task.run(), initialDelayTicks, periodTicks);
    }

    /**
     * Teleport a player using Paper's async teleport API (Folia-safe).
     * Runs onSuccess on the player's region thread after a successful teleport.
     */
    public static void teleport(Plugin plugin, Player player, Location destination, Runnable onSuccess) {
        player.teleportAsync(destination).thenAccept(success -> {
            if (success && onSuccess != null) {
                runForEntity(plugin, player, onSuccess);
            }
        });
    }
}
