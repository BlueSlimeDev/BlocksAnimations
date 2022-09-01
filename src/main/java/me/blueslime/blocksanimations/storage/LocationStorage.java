package me.blueslime.blocksanimations.storage;

import org.bukkit.Location;

import java.util.UUID;

public class LocationStorage {
    private final PluginStorage<UUID, Location> pos1 = PluginStorage.initAsConcurrentHash();

    private final PluginStorage<UUID, Location> pos2 = PluginStorage.initAsConcurrentHash();


    public void remove(UUID uuid) {
        if (pos1.toMap().containsKey(uuid) || pos2.toMap().containsKey(uuid)) {
            pos1.remove(uuid);
            pos2.remove(uuid);
        }
    }

    public boolean isCompleted(UUID uuid) {
        return pos1.toMap().containsKey(uuid) && pos2.toMap().containsKey(uuid);
    }

    public Location getFirstPosition(UUID uuid) {
        if (!pos1.toMap().containsKey(uuid)) {
            return null;
        }
        return pos1.get(uuid);
    }

    public Location getSecondPosition(UUID uuid) {
        if (!pos2.toMap().containsKey(uuid)) {
            return null;
        }
        return pos2.get(uuid);
    }

    public void setFirstPosition(UUID uuid, Location location) {
        pos1.add(uuid, location);
    }

    public void setSecondPosition(UUID uuid, Location location) {
        pos2.add(uuid, location);
    }



}
