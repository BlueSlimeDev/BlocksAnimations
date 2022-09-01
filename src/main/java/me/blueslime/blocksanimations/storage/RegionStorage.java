package me.blueslime.blocksanimations.storage;

import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.regions.Region;

public class RegionStorage {

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
        regions.clear();

        ConfigurationHandler region = plugin.getConfigurationHandler(SlimeFile.BLOCKS);

        for (String name : region.getContent("regions", false)) {

            regions.add(
                    name,
                    new Region(
                            plugin,
                            name
                    )
            );
        }
    }


    public PluginStorage<String, Region> getRegions() {
        return regions;
    }
}
