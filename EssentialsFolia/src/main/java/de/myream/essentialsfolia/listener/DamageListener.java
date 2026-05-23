package de.myream.essentialsfolia.listener;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DamageListener implements Listener {

    private final EssentialsFolia plugin;

    public DamageListener(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        EssentialsUser user = plugin.getUserManager().get(player.getUniqueId());
        if (user != null && user.isGodMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EssentialsUser user = plugin.getUserManager().get(player.getUniqueId());
        if (user != null) {
            user.setLastDeathLocation(player.getLocation());
        }
    }
}
