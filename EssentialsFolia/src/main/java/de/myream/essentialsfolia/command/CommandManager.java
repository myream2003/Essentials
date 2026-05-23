package de.myream.essentialsfolia.command;

import de.myream.essentialsfolia.EssentialsFolia;
import de.myream.essentialsfolia.command.chat.BroadcastCommand;
import de.myream.essentialsfolia.command.chat.MsgCommand;
import de.myream.essentialsfolia.command.chat.ReplyCommand;
import de.myream.essentialsfolia.command.home.DelHomeCommand;
import de.myream.essentialsfolia.command.home.HomeCommand;
import de.myream.essentialsfolia.command.home.HomesCommand;
import de.myream.essentialsfolia.command.home.SetHomeCommand;
import de.myream.essentialsfolia.command.kit.KitCommand;
import de.myream.essentialsfolia.command.moderation.BanCommand;
import de.myream.essentialsfolia.command.moderation.KickCommand;
import de.myream.essentialsfolia.command.moderation.MuteCommand;
import de.myream.essentialsfolia.command.moderation.UnbanCommand;
import de.myream.essentialsfolia.command.moderation.UnmuteCommand;
import de.myream.essentialsfolia.command.player.AfkCommand;
import de.myream.essentialsfolia.command.player.FeedCommand;
import de.myream.essentialsfolia.command.player.FlyCommand;
import de.myream.essentialsfolia.command.player.GodCommand;
import de.myream.essentialsfolia.command.player.HealCommand;
import de.myream.essentialsfolia.command.player.NickCommand;
import de.myream.essentialsfolia.command.player.PingCommand;
import de.myream.essentialsfolia.command.player.SpeedCommand;
import de.myream.essentialsfolia.command.player.VanishCommand;
import de.myream.essentialsfolia.command.teleport.BackCommand;
import de.myream.essentialsfolia.command.teleport.SetSpawnCommand;
import de.myream.essentialsfolia.command.teleport.SpawnCommand;
import de.myream.essentialsfolia.command.teleport.TpCommand;
import de.myream.essentialsfolia.command.teleport.TpaCommand;
import de.myream.essentialsfolia.command.teleport.TpacceptCommand;
import de.myream.essentialsfolia.command.teleport.TpahereCommand;
import de.myream.essentialsfolia.command.teleport.TpdenyCommand;
import de.myream.essentialsfolia.command.warp.DelWarpCommand;
import de.myream.essentialsfolia.command.warp.SetWarpCommand;
import de.myream.essentialsfolia.command.warp.WarpCommand;
import de.myream.essentialsfolia.command.warp.WarpsCommand;
import de.myream.essentialsfolia.command.world.GamemodeCommand;
import de.myream.essentialsfolia.command.world.GiveCommand;
import de.myream.essentialsfolia.command.world.ItemCommand;
import de.myream.essentialsfolia.command.world.KillCommand;
import de.myream.essentialsfolia.command.world.TimeCommand;
import de.myream.essentialsfolia.command.world.TpsCommand;
import de.myream.essentialsfolia.command.world.UptimeCommand;
import de.myream.essentialsfolia.command.world.WeatherCommand;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    private final EssentialsFolia plugin;

    public CommandManager(EssentialsFolia plugin) {
        this.plugin = plugin;
    }

    public void register() {
        // Teleport
        reg("tp", new TpCommand(plugin));
        reg("tpa", new TpaCommand(plugin));
        reg("tpahere", new TpahereCommand(plugin));
        reg("tpaccept", new TpacceptCommand(plugin));
        reg("tpdeny", new TpdenyCommand(plugin));
        reg("back", new BackCommand(plugin));
        reg("spawn", new SpawnCommand(plugin));
        reg("setspawn", new SetSpawnCommand(plugin));

        // Homes
        reg("home", new HomeCommand(plugin));
        reg("sethome", new SetHomeCommand(plugin));
        reg("delhome", new DelHomeCommand(plugin));
        reg("homes", new HomesCommand(plugin));

        // Warps
        reg("warp", new WarpCommand(plugin));
        reg("setwarp", new SetWarpCommand(plugin));
        reg("delwarp", new DelWarpCommand(plugin));
        reg("warps", new WarpsCommand(plugin));

        // Player
        reg("heal", new HealCommand(plugin));
        reg("feed", new FeedCommand(plugin));
        reg("fly", new FlyCommand(plugin));
        reg("god", new GodCommand(plugin));
        reg("speed", new SpeedCommand(plugin));
        reg("vanish", new VanishCommand(plugin));
        reg("nick", new NickCommand(plugin));
        reg("afk", new AfkCommand(plugin));
        reg("ping", new PingCommand(plugin));

        // Chat
        reg("msg", new MsgCommand(plugin));
        reg("reply", new ReplyCommand(plugin));
        reg("broadcast", new BroadcastCommand(plugin));

        // World / Gamemode shortcuts
        GamemodeCommand gmCmd = new GamemodeCommand(plugin);
        reg("gamemode", gmCmd);
        reg("gms", new GamemodeCommand(plugin, "survival"));
        reg("gmc", new GamemodeCommand(plugin, "creative"));
        reg("gma", new GamemodeCommand(plugin, "adventure"));
        reg("gmsp", new GamemodeCommand(plugin, "spectator"));
        reg("give", new GiveCommand(plugin));
        reg("item", new ItemCommand(plugin));
        reg("time", new TimeCommand(plugin));
        reg("weather", new WeatherCommand(plugin));
        reg("kill", new KillCommand(plugin));

        // Kit
        KitCommand kitCmd = new KitCommand(plugin);
        reg("kit", kitCmd);
        reg("kits", kitCmd);

        // Moderation
        reg("kick", new KickCommand(plugin));
        reg("ban", new BanCommand(plugin));
        reg("unban", new UnbanCommand(plugin));
        reg("mute", new MuteCommand(plugin));
        reg("unmute", new UnmuteCommand(plugin));

        // Admin / Info
        reg("essentials", new EssentialsAdminCommand(plugin));
        reg("tps", new TpsCommand(plugin));
        reg("uptime", new UptimeCommand(plugin));
    }

    private void reg(String name, AbstractCommand handler) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd == null) {
            plugin.getLogger().warning("Command '" + name + "' not found in plugin.yml!");
            return;
        }
        cmd.setExecutor(handler);
        cmd.setTabCompleter(handler);
    }
}
