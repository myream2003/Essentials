package de.myream.essentialsfolia.command.home;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.model.EssentialsUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends AbstractCommand {

    public SetHomeCommand(EssentialsFolia plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePlayer(sender) || !requirePermission(sender, "essentials.sethome")) return;

        Player self = asPlayer(sender);
        EssentialsUser user = plugin.getUserManager().get(self);
        String homeName = args.length > 0 ? args[0] : "home";

        // Check home limit
        int maxHomes = getMaxHomes(self);
        if (!user.hasHome(homeName) && user.getHomes().size() >= maxHomes) {
            send(sender, "home-limit-reached", maxHomes);
            return;
        }

        user.setHome(homeName, self.getLocation());
        plugin.getUserManager().save(user);
        send(sender, "home-set", homeName);
    }

    private int getMaxHomes(Player player) {
        if (player.hasPermission("essentials.homes.unlimited")) return Integer.MAX_VALUE;
        String prefix = plugin.getConfig().getString("homes.limit-permission-prefix", "essentials.homes.");
        int max = plugin.getConfig().getInt("homes.max-homes", 1);
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission(prefix + i)) return i;
        }
        return max;
    }
}
