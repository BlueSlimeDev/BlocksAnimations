package me.blueslime.blocksanimations.regions;

import com.cryptomorin.xseries.XMaterial;
import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import dev.mruniverse.slimelib.file.configuration.handlers.bukkit.BukkitConfigurationHandler;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.regions.area.Cuboid;
import me.blueslime.blocksanimations.regions.runnable.RegionRunnable;
import me.blueslime.blocksanimations.utils.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Region {
    private final Map<Integer, Map<Location, ItemStack>> blockMap = new ConcurrentHashMap<>();
    private final BlocksAnimations plugin;
    private final Cuboid cuboid;

    private RegionRunnable runnable = null;
    private final String name;

    public Region(BlocksAnimations plugin, String name) {
        ConfigurationHandler configuration = plugin.getConfigurationHandler(SlimeFile.BLOCKS);

        this.plugin = plugin;

        BukkitConfigurationHandler bukkitConfig = (BukkitConfigurationHandler)configuration;

        FileConfiguration config = bukkitConfig.toSpecifiedConfiguration();

        this.cuboid = new Cuboid(
                LocationSerializer.fromString(
                        plugin.getServer(),
                        config.getString("regions." + name + ".cuboid.location-1", "world, 0, 0, 0")
                ),
                LocationSerializer.fromString(
                        plugin.getServer(),
                        config.getString("regions." + name + ".cuboid.location-2", "world, 0, 0, 0")
                )
        );

        this.name   = name;

        load(configuration);
    }

    public void load(ConfigurationHandler configuration) {
        blockMap.clear();

        boolean debugMode = isDebug();

        for (String key : configuration.getContent("regions." + name + ".area-templates", false)) {

            int id = Integer.parseInt(key);

            Map<Location, ItemStack> bMap = blockMap.computeIfAbsent(
                    id,
                    F -> new ConcurrentHashMap<>()
            );

            if (debugMode) {
                plugin.getLogs().info(
                        "Created a new map for area-id: " + id
                );
            }

            for (String blocks : configuration.getStringList("regions." + name + ".area-templates." + id)) {
                String[] split = blocks.replace(" ", "").split(",");

                if (debugMode) {
                    plugin.getLogs().info(
                            "Loading blocks for area-id: " + id
                    );
                }

                if (split.length != 5) {
                    if (debugMode) {
                        plugin.getLogs().info(
                                "An strange block has been found in area-id: " + id + ", original:" + blocks
                        );
                    }
                    continue;
                }

                Optional<XMaterial> material = XMaterial.matchXMaterial(split[0]);

                if (!material.isPresent()) {
                    if (debugMode) {
                        plugin.getLogs().info(
                                "Don't found block materials of area-id: " + id + ", cause:" + blocks
                        );
                    }
                    continue;
                }

                Location location = LocationSerializer.fromString(
                        plugin.getServer(),
                        blocks.replace(split[0] + ", ", "")
                                .replace(split[0] + " ,", "")
                                .replace(split[0] + ",", "")
                );

                ItemStack item = material.get().parseItem();

                if (item == null) {
                    plugin.getLogs().info("Detected null item while the plugin is trying to load");
                    plugin.getLogs().info("the area-id: " + id + ", in region: " + name);
                    continue;
                }

                if (debugMode) {
                    plugin.getLogs().info(
                            "Loaded block location: " + location.toString() + ", Block: " + item
                    );
                }

                bMap.put(
                        location,
                        item
                );
            }

        }

    }

    public SlimeLogs getLogs() {
        return plugin.getLogs();
    }

    public String getName() {
        return name;
    }

    public boolean isDebug() {
        return plugin.getConfigurationHandler(SlimeFile.SETTINGS).getBoolean("settings.debug-mode", false);
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public RegionRunnable getRunnable() {
        return runnable;
    }

    public Map<Integer, Map<Location, ItemStack>> getBlockMap() {
        return blockMap;
    }

    public void start() {
        cancel();

        load(
                plugin.getConfigurationHandler(SlimeFile.BLOCKS)
        );

        runnable = new RegionRunnable(this, isDebug());

        runnable.runTaskTimer(
                plugin,
                0L,
                Long.parseLong(
                        plugin.getConfigurationHandler(SlimeFile.BLOCKS).getString("regions." + name + ".update-delay", "20")
                )
        );
    }

    public void pause() {
        cancel();
    }

    public void cancel() {
        if (runnable != null) {
            try {
                runnable.cancel();
            } catch (IllegalStateException ignored) {}
        }
    }

}
