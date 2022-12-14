package me.blueslime.blocksanimations.storage;

import org.bukkit.Location;

import java.util.UUID;

public class LocationStorage {
    private final PluginStorage<UUID, Location> pos1 = PluginStorage.initAsConcurrentHash();

    private final PluginStorage<UUID, Location> pos2 = PluginStorage.initAsConcurrentHash();


    public void remove(UUID uuid) {
        if (pos1.contains(uuid) || pos2.contains(uuid)) {
            pos1.remove(uuid);
            pos2.remove(uuid);
        }
    }

    public boolean isCompleted(UUID uuid) {
        return pos1.contains(uuid) && pos2.contains(uuid);
    }

    public Location getFirstPosition(UUID uuid) {
        if (!pos1.contains(uuid)) {
            return null;
        }
        return pos1.get(uuid);
    }

    public Location getSecondPosition(UUID uuid) {
        if (!pos2.contains(uuid)) {
            return null;
        }
        return pos2.get(uuid);
    }

    public void setFirstPosition(UUID uuid, Location location) {
        pos1.toMap().put(uuid, location);
    }

    public void setSecondPosition(UUID uuid, Location location) {
        pos2.toMap().put(uuid, location);
    }



}
