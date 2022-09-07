package me.blueslime.blocksanimations.storage;

import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.regions.Region;
import me.blueslime.blocksanimations.regions.RegionType;
import org.bukkit.Location;

public class RegionStorage {
    private final PluginStorage<Location, String> locationMap = PluginStorage.initAsConcurrentHash();
    private final PluginStorage<String, Region> regions = PluginStorage.initAsConcurrentHash();

    private final BlocksAnimations plugin;

    public RegionStorage(BlocksAnimations plugin) {
        this.plugin = plugin;
        load();
    }

    public void restart() {
        load();
    }

    private void load() {
        locationMap.clear();
        regions.clear();

        ConfigurationHandler region = plugin.getConfigurationHandler(SlimeFile.BLOCKS);

        for (String name : region.getContent("regions", false)) {

            boolean enabled = region.getStatus("regions." + name + ".enabled", true);

            if (enabled) {
                plugin.getLogs().info("Loading region '&a" + name + "&f'");

                boolean start = region.getStatus("regions." + name + ".start-runnable-automatically", false);

                Region reg = new Region(
                        plugin,
                        name
                );

                regions.add(
                        name,
                        reg
                );

                if (start && reg.getType() != RegionType.INTERACT) {
                    reg.start();
                    plugin.getLogs().info("Starting animation of " + name);
                }
            } else {
                plugin.getLogs().info("Region " + name + " will not be loaded because is disabled!");
            }
        }
    }

    public Region fromLocationMap(Location location) {
        return getRegions().get(
                getLocationMap().get(location)
        );
    }

    public PluginStorage<Location, String> getLocationMap() {
        return locationMap;
    }

    public PluginStorage<String, Region> getRegions() {
        return regions;
    }
}
