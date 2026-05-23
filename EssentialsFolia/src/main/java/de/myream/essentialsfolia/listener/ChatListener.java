package de.myream.essentialsfolia.listener;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.model.EssentialsUser;
import de.myream.essentialsfolia.util.Colors;
import de.myream.essentialsfolia.util.FoliaLib;
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

        if (user.isMuted()) {
            event.setCancelled(true);
            player.sendMessage(Colors.parse(plugin.msgRaw("mute-is-muted")));
            return;
        }

        if (user.isAfk()) {
            user.setAfk(false);
            user.setAfkMessage("");
            // server.broadcast is thread-safe in Paper; capture display name here while on async thread
            String displayName = player.getDisplayName();
            FoliaLib.runGlobal(plugin,
                    () -> plugin.getServer().broadcast(
                            Colors.parse(plugin.msgRaw("afk-back", displayName))));
        }
    }
}
