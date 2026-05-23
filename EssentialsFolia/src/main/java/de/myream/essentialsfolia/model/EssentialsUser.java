package de.myream.essentialsfolia.model;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class EssentialsUser {

    private final UUID uuid;
    private volatile String name;

    private final Map<String, Location> homes = new HashMap<>();
    private volatile Location lastLocation;
    private volatile Location lastDeathLocation;

    private final AtomicBoolean godMode = new AtomicBoolean(false);
    private final AtomicBoolean flyMode = new AtomicBoolean(false);
    private final AtomicBoolean vanished = new AtomicBoolean(false);
    private final AtomicBoolean afk = new AtomicBoolean(false);
    private volatile String afkMessage = "";
    private volatile String nickname = null;

    private volatile UUID lastMessageTarget = null;
    private final AtomicBoolean muted = new AtomicBoolean(false);
    private final AtomicLong muteExpiry = new AtomicLong(0);

    private final Map<String, Long> kitCooldowns = new HashMap<>();
    private volatile long lastTeleportTime = 0;

    public EssentialsUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public synchronized Map<String, Location> getHomes() { return homes; }
    public synchronized Location getHome(String name) { return homes.get(name.toLowerCase()); }
    public synchronized void setHome(String name, Location loc) { homes.put(name.toLowerCase(), loc); }
    public synchronized boolean deleteHome(String name) { return homes.remove(name.toLowerCase()) != null; }
    public synchronized boolean hasHome(String name) { return homes.containsKey(name.toLowerCase()); }

    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location loc) { this.lastLocation = loc; }

    public Location getLastDeathLocation() { return lastDeathLocation; }
    public void setLastDeathLocation(Location loc) { this.lastDeathLocation = loc; }

    public boolean isGodMode() { return godMode.get(); }
    public void setGodMode(boolean value) { godMode.set(value); }

    public boolean isFlyMode() { return flyMode.get(); }
    public void setFlyMode(boolean value) { flyMode.set(value); }

    public boolean isVanished() { return vanished.get(); }
    public void setVanished(boolean value) { vanished.set(value); }

    public boolean isAfk() { return afk.get(); }
    public void setAfk(boolean value) { afk.set(value); }

    public String getAfkMessage() { return afkMessage; }
    public void setAfkMessage(String msg) { this.afkMessage = msg != null ? msg : ""; }

    public String getNickname() { return nickname; }
    public void setNickname(String nick) { this.nickname = nick; }

    public UUID getLastMessageTarget() { return lastMessageTarget; }
    public void setLastMessageTarget(UUID target) { this.lastMessageTarget = target; }

    /** Thread-safe mute check; auto-expires timed mutes. */
    public boolean isMuted() {
        if (!muted.get()) return false;
        long expiry = muteExpiry.get();
        if (expiry > 0 && System.currentTimeMillis() > expiry) {
            muted.set(false);
            muteExpiry.set(0);
            return false;
        }
        return true;
    }

    public void setMuted(boolean value) { muted.set(value); }
    public long getMuteExpiry() { return muteExpiry.get(); }
    public void setMuteExpiry(long expiry) { muteExpiry.set(expiry); }

    public synchronized Map<String, Long> getKitCooldowns() { return kitCooldowns; }
    public synchronized long getKitCooldown(String kitName) { return kitCooldowns.getOrDefault(kitName.toLowerCase(), 0L); }
    public synchronized void setKitCooldown(String kitName, long time) { kitCooldowns.put(kitName.toLowerCase(), time); }

    public long getLastTeleportTime() { return lastTeleportTime; }
    public void setLastTeleportTime(long time) { this.lastTeleportTime = time; }
}
