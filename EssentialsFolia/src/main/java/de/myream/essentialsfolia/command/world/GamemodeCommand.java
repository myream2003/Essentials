package de.myream.essentialsfolia.command.world;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.AbstractCommand;
import de.myream.essentialsfolia.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand extends AbstractCommand {

    private final String forced; // pre-set gamemode for gms/gmc/gma/gmsp shortcuts

    public GamemodeCommand(EssentialsFolia plugin) {
        super(plugin);
        this.forced = null;
    }

    public GamemodeCommand(EssentialsFolia plugin, String forced) {
        super(plugin);
        this.forced = forced;
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        if (!requirePermission(sender, "essentials.gamemode")) return;

        String modeStr;
        Player target;

        if (forced != null) {
            modeStr = forced;
            if (args.length > 0) {
                if (!sender.hasPermission("essentials.gamemode.others")) {
                    sendRaw(sender, "no-permission");
                    return;
                }
                target = Bukkit.getPlayerExact(args[0]);
                if (target == null) { send(sender, "player-not-found", args[0]); return; }
            } else {
                if (!requirePlayer(sender)) return;
                target = asPlayer(sender);
            }
        } else {
            if (args.length == 0) {
                sendRaw(sender, "invalid-usage", "/gamemode <survival|creative|adventure|spectator> [player]");
                return;
            }
            modeStr = args[0];
            if (args.length > 1) {
                if (!sender.hasPermission("essentials.gamemode.others")) {
                    sendRaw(sender, "no-permission");
                    return;
                }
                target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { send(sender, "player-not-found", args[1]); return; }
            } else {
                if (!requirePlayer(sender)) return;
                target = asPlayer(sender);
            }
        }

        GameMode gm = parseGameMode(modeStr);
        if (gm == null) {
            sendRaw(sender, "invalid-usage", "Ungültiger Spielermodus: " + modeStr);
            return;
        }

        target.setGameMode(gm);
        String gmName = gm.name().toLowerCase();

        if (target.equals(sender)) {
            send(sender, "gamemode-set-self", gmName);
        } else {
            send(sender, "gamemode-set-other", target.getName(), gmName);
            target.sendMessage(Colors.parse(plugin.msg("gamemode-set-received", sender.getName(), gmName)));
        }
    }

    private GameMode parseGameMode(String s) {
        return switch (s.toLowerCase()) {
            case "survival", "s", "0" -> GameMode.SURVIVAL;
            case "creative", "c", "1" -> GameMode.CREATIVE;
            case "adventure", "a", "2" -> GameMode.ADVENTURE;
            case "spectator", "sp", "3" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (forced != null) {
            if (args.length == 1 && sender.hasPermission("essentials.gamemode.others")) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
                return names;
            }
            return List.of();
        }
        if (args.length == 1) return List.of("survival", "creative", "adventure", "spectator");
        if (args.length == 2 && sender.hasPermission("essentials.gamemode.others")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
}
