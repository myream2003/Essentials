package de.myream.essentialsfolia.listener;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final EssentialsFolia plugin;

    public ChatListener(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        EssentialsUser user = plugin.getUserManager().get(player.getUniqueId());
        if (user == null) return;

        // Block muted players
        if (user.isMuted()) {
            event.setCancelled(true);
            player.sendMessage(Colors.parse(plugin.msgRaw("mute-is-muted")));
            return;
        }

        // Unset AFK on chat
        if (user.isAfk()) {
            user.setAfk(false);
            user.setAfkMessage("");
            String msg = plugin.msgRaw("afk-back", player.getDisplayName());
            plugin.getServer().broadcast(Colors.parse(msg));
        }
    }
}
