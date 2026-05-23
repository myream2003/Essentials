package de.myream.essentialsfolia.listener;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private final EssentialsFolia plugin;

    public PlayerJoinQuitListener(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        EssentialsUser user = plugin.getUserManager().getOrCreate(player);
        user.setName(player.getName());

        // Restore fly mode
        if (user.isFlyMode() && player.hasPermission("essentials.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        // Apply nickname
        if (user.getNickname() != null) {
            player.setDisplayName(Colors.colorize(user.getNickname()));
        }

        // Restore vanish
        if (user.isVanished() && player.hasPermission("essentials.vanish")) {
            for (Player other : plugin.getServer().getOnlinePlayers()) {
                if (!other.hasPermission("essentials.vanish.see")) {
                    other.hidePlayer(plugin, player);
                }
            }
        }

        // Hide vanished players from this player
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            EssentialsUser otherUser = plugin.getUserManager().get(other.getUniqueId());
            if (otherUser != null && otherUser.isVanished() && !player.hasPermission("essentials.vanish.see")) {
                player.hidePlayer(plugin, other);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Cancel pending teleports
        plugin.getTeleportManager().cancelPending(player.getUniqueId());

        // Save and unload user data
        plugin.getUserManager().unload(player.getUniqueId());
    }
}
