package de.myream.essentialsfolia.listener;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMoveListener implements Listener {

    private final EssentialsFolia plugin;
    private final Map<UUID, Long> lastMove = new ConcurrentHashMap<>();

    public PlayerMoveListener(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        // Only care about actual block movement, not head rotation
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        lastMove.put(uuid, System.currentTimeMillis());

        EssentialsUser user = plugin.getUserManager().get(uuid);
        if (user == null) return;

        // Unset AFK on movement
        if (user.isAfk()) {
            user.setAfk(false);
            user.setAfkMessage("");
            String msg = plugin.msgRaw("afk-back", player.getDisplayName());
            plugin.getServer().broadcast(Colors.parse(msg));
        }
    }

    public long getLastMove(UUID uuid) {
        return lastMove.getOrDefault(uuid, System.currentTimeMillis());
    }
}
