package de.myream.essentialsfolia.model;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EssentialsUser {

    private final UUID uuid;
    private String name;

    private final Map<String, Location> homes = new HashMap<>();
    private Location lastLocation;
    private Location lastDeathLocation;

    private boolean godMode = false;
    private boolean flyMode = false;
    private boolean vanished = false;
    private boolean afk = false;
    private String afkMessage = "";
    private String nickname = null;

    private UUID lastMessageTarget = null;
    private boolean muted = false;
    private long muteExpiry = 0;

    private final Map<String, Long> kitCooldowns = new HashMap<>();
    private long lastTeleportTime = 0;

    public EssentialsUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Location> getHomes() { return homes; }
    public Location getHome(String name) { return homes.get(name.toLowerCase()); }
    public void setHome(String name, Location loc) { homes.put(name.toLowerCase(), loc); }
    public boolean deleteHome(String name) { return homes.remove(name.toLowerCase()) != null; }
    public boolean hasHome(String name) { return homes.containsKey(name.toLowerCase()); }

    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location loc) { this.lastLocation = loc; }

    public Location getLastDeathLocation() { return lastDeathLocation; }
    public void setLastDeathLocation(Location loc) { this.lastDeathLocation = loc; }

    public boolean isGodMode() { return godMode; }
    public void setGodMode(boolean godMode) { this.godMode = godMode; }

    public boolean isFlyMode() { return flyMode; }
    public void setFlyMode(boolean flyMode) { this.flyMode = flyMode; }

    public boolean isVanished() { return vanished; }
    public void setVanished(boolean vanished) { this.vanished = vanished; }

    public boolean isAfk() { return afk; }
    public void setAfk(boolean afk) { this.afk = afk; }

    public String getAfkMessage() { return afkMessage; }
    public void setAfkMessage(String msg) { this.afkMessage = msg; }

    public String getNickname() { return nickname; }
    public void setNickname(String nick) { this.nickname = nick; }

    public UUID getLastMessageTarget() { return lastMessageTarget; }
    public void setLastMessageTarget(UUID target) { this.lastMessageTarget = target; }

    public boolean isMuted() {
        if (muted && muteExpiry > 0 && System.currentTimeMillis() > muteExpiry) {
            muted = false;
        }
        return muted;
    }

    public void setMuted(boolean muted) { this.muted = muted; }
    public long getMuteExpiry() { return muteExpiry; }
    public void setMuteExpiry(long expiry) { this.muteExpiry = expiry; }

    public Map<String, Long> getKitCooldowns() { return kitCooldowns; }

    public long getKitCooldown(String kitName) {
        return kitCooldowns.getOrDefault(kitName.toLowerCase(), 0L);
    }

    public void setKitCooldown(String kitName, long time) {
        kitCooldowns.put(kitName.toLowerCase(), time);
    }

    public long getLastTeleportTime() { return lastTeleportTime; }
    public void setLastTeleportTime(long time) { this.lastTeleportTime = time; }
}
