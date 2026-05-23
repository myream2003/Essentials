package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import de.myream.essentialsfolia.util.FoliaLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TeleportManager {

    private final EssentialsFolia plugin;

    // TPA:     requester UUID → target UUID
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    // TPAHere: requester UUID → target UUID
    private final Map<UUID, UUID> tpahereRequests = new ConcurrentHashMap<>();
    // Countdown: player UUID → [blockX, blockY, blockZ] at teleport start
    private final Map<UUID, int[]> pendingTeleports = new ConcurrentHashMap<>();

    public TeleportManager(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    /**
     * Teleport a player to a destination with optional countdown.
     * Cancels the teleport if the player moves during the countdown.
     */
    public void teleport(Player player, Location destination, Runnable onSuccess) {
        int delay = plugin.getConfig().getInt("teleport.delay", 3);
        boolean cancelOnMove = plugin.getConfig().getBoolean("teleport.cancel-on-move", true);

        if (delay <= 0) {
            FoliaLib.teleport(plugin, player, destination, onSuccess);
            return;
        }

        Location start = player.getLocation();
        pendingTeleports.put(player.getUniqueId(),
                new int[]{start.getBlockX(), start.getBlockY(), start.getBlockZ()});

        countdown(player, destination, delay, cancelOnMove, onSuccess);
    }

    private void countdown(Player player, Location destination, int remaining, boolean cancelOnMove, Runnable onSuccess) {
        if (!player.isOnline()) {
            pendingTeleports.remove(player.getUniqueId());
            return;
        }

        if (cancelOnMove) {
            int[] startCoords = pendingTeleports.get(player.getUniqueId());
            if (startCoords != null) {
                Location cur = player.getLocation();
                if (cur.getBlockX() != startCoords[0]
                        || cur.getBlockY() != startCoords[1]
                        || cur.getBlockZ() != startCoords[2]) {
                    pendingTeleports.remove(player.getUniqueId());
                    player.sendMessage(Colors.parse(plugin.msg("tp-cancelled")));
                    return;
                }
            }
        }

        if (remaining <= 0) {
            pendingTeleports.remove(player.getUniqueId());
            FoliaLib.teleport(plugin, player, destination, onSuccess);
            return;
        }

        player.sendMessage(Colors.parse(plugin.msg("tp-countdown", remaining)));

        FoliaLib.runForEntityLater(plugin, player,
                () -> countdown(player, destination, remaining - 1, cancelOnMove, onSuccess),
                20L);
    }

    public void cancelPending(UUID uuid) {
        pendingTeleports.remove(uuid);
    }

    public boolean hasPending(UUID uuid) {
        return pendingTeleports.containsKey(uuid);
    }

    // --- TPA ---

    public void sendTpaRequest(Player from, Player to) {
        tpaRequests.put(from.getUniqueId(), to.getUniqueId());
        int expire = plugin.getConfig().getInt("teleport.tpa-expire", 60);
        FoliaLib.runAsyncLater(plugin,
                () -> tpaRequests.remove(from.getUniqueId()),
                expire, TimeUnit.SECONDS);
    }

    public void sendTpahereRequest(Player from, Player to) {
        tpahereRequests.put(from.getUniqueId(), to.getUniqueId());
        int expire = plugin.getConfig().getInt("teleport.tpa-expire", 60);
        FoliaLib.runAsyncLater(plugin,
                () -> tpahereRequests.remove(from.getUniqueId()),
                expire, TimeUnit.SECONDS);
    }

    /** Returns the UUID of the player who sent a TPA request to target, or null. */
    public UUID getPendingTpaFor(UUID target) {
        for (Map.Entry<UUID, UUID> entry : tpaRequests.entrySet()) {
            if (entry.getValue().equals(target)) return entry.getKey();
        }
        return null;
    }

    /** Returns the UUID of the player who sent a TPAHere request to target, or null. */
    public UUID getPendingTpahereFor(UUID target) {
        for (Map.Entry<UUID, UUID> entry : tpahereRequests.entrySet()) {
            if (entry.getValue().equals(target)) return entry.getKey();
        }
        return null;
    }

    public void clearTpaRequest(UUID requester) {
        tpaRequests.remove(requester);
    }

    public void clearTpahereRequest(UUID requester) {
        tpahereRequests.remove(requester);
    }

    public void saveBack(EssentialsUser user, Location location) {
        user.setLastLocation(location);
    }
}
