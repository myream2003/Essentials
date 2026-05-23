package de.myream.essentialsfolia.command;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected final EssentialsFolia plugin;

    protected AbstractCommand(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, label, args);
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = tabComplete(sender, label, args);
        return completions != null ? completions : Collections.emptyList();
    }

    protected abstract void execute(CommandSender sender, String label, String[] args);

    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }

    protected boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Colors.parse(plugin.msgRaw("player-only")));
            return false;
        }
        return true;
    }

    protected boolean requirePermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Colors.parse(plugin.msgRaw("no-permission")));
            return false;
        }
        return true;
    }

    protected void send(CommandSender sender, String messageKey, Object... args) {
        sender.sendMessage(Colors.parse(plugin.msg(messageKey, args)));
    }

    protected void sendRaw(CommandSender sender, String messageKey, Object... args) {
        sender.sendMessage(Colors.parse(plugin.msgRaw(messageKey, args)));
    }

    protected Player asPlayer(CommandSender sender) {
        return (Player) sender;
    }
}
