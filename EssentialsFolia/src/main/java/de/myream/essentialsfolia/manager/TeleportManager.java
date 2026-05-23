package de.myream.essentialsfolia.manager;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.FoliaLib;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TeleportManager {

    private final EssentialsFolia plugin;

    // TPA: requester -> target
    private final Map<UUID, UUID> tpaRequests = new ConcurrentHashMap<>();
    // TPAHere: requester -> target
    private final Map<UUID, UUID> tpahereRequests = new ConcurrentHashMap<>();
    // Players currently in teleport countdown
    private final Map<UUID, Location> pendingTeleports = new ConcurrentHashMap<>();

    public TeleportManager(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    /**
     * Teleport a player to a destination with optional countdown.
     * Cancels if the player moves during the countdown (configurable).
     */
    public void teleport(Player player, Location destination, Runnable onSuccess) {
        int delay = plugin.getConfig().getInt("teleport.delay", 3);
        boolean cancelOnMove = plugin.getConfig().getBoolean("teleport.cancel-on-move", true);

        if (delay <= 0) {
            FoliaLib.teleport(plugin, player, destination, onSuccess);
            return;
        }

        pendingTeleports.put(player.getUniqueId(), player.getLocation());

        countdown(player, destination, delay, cancelOnMove, onSuccess);
    }

    private void countdown(Player player, Location destination, int remaining, boolean cancelOnMove, Runnable onSuccess) {
        if (!player.isOnline()) {
            pendingTeleports.remove(player.getUniqueId());
            return;
        }

        if (cancelOnMove) {
            Location startLoc = pendingTeleports.get(player.getUniqueId());
            if (startLoc != null && !isSameBlock(startLoc, player.getLocation())) {
                pendingTeleports.remove(player.getUniqueId());
                player.sendMessage(Colors.parse(plugin.getMessages().getString(
                        "tp-cancelled", "&cTeleport abgebrochen! Du hast dich bewegt.")));
                return;
            }
        }

        if (remaining <= 0) {
            pendingTeleports.remove(player.getUniqueId());
            FoliaLib.teleport(plugin, player, destination, onSuccess);
            return;
        }

        String msg = plugin.getMessages().getString("tp-countdown", "&eTeleportiere in &6{0} &eSekunde(n)...")
                .replace("{0}", String.valueOf(remaining));
        player.sendMessage(Colors.parse(msg));

        FoliaLib.runForEntityLater(plugin, player,
                () -> countdown(player, destination, remaining - 1, cancelOnMove, onSuccess), 20L);
    }

    public boolean isSameBlock(Location a, Location b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
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
        FoliaLib.runAsyncLater(plugin, () -> tpaRequests.remove(from.getUniqueId()), expire, TimeUnit.SECONDS);
    }

    public void sendTpahereRequest(Player from, Player to) {
        tpahereRequests.put(from.getUniqueId(), to.getUniqueId());
        int expire = plugin.getConfig().getInt("teleport.tpa-expire", 60);
        FoliaLib.runAsyncLater(plugin, () -> tpahereRequests.remove(from.getUniqueId()), expire, TimeUnit.SECONDS);
    }

    /** Returns the UUID of the player who sent a TPA request to the given player, or null. */
    public UUID getPendingTpaFor(UUID target) {
        for (Map.Entry<UUID, UUID> entry : tpaRequests.entrySet()) {
            if (entry.getValue().equals(target)) return entry.getKey();
        }
        return null;
    }

    /** Returns the UUID of the player who sent a TPAHere request to the given player, or null. */
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

    public boolean hasTpaRequestTo(UUID from, UUID to) {
        UUID target = tpaRequests.get(from);
        return target != null && target.equals(to);
    }

    public void saveBack(EssentialsUser user, Location location) {
        user.setLastLocation(location);
    }
}
