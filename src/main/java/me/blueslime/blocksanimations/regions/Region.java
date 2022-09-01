package me.blueslime.blocksanimations.regions;

import com.cryptomorin.xseries.XMaterial;
import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.regions.area.Cuboid;
import me.blueslime.blocksanimations.regions.runnable.RegionRunnable;
import me.blueslime.blocksanimations.utils.LocationSerializer;
import org.bukkit.Location;
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

        this.cuboid = new Cuboid(
                LocationSerializer.fromString(
                        configuration.getString("regions." + name + ".cuboid.location-1", "world, 0, 0, 0")
                ),
                LocationSerializer.fromString(
                        configuration.getString("regions." + name + ".cuboid.location-2", "world, 0, 0, 0")
                )
        );

        this.name   = name;

        load(configuration);
    }

    private void load(ConfigurationHandler configuration) {
        blockMap.clear();

        for (String key : configuration.getContent("regions." + name + ".area-templates", false)) {

            int id = Integer.parseInt(key);

            Map<Location, ItemStack> bMap = blockMap.computeIfAbsent(
                    id,
                    F -> new ConcurrentHashMap<>()
            );

            for (String blocks : configuration.getStringList("regions." + name + ".area-templates." + id)) {
                String[] split = blocks.replace(" ", "").split(",");

                if (split.length != 4) {
                    continue;
                }

                Optional<XMaterial> material = XMaterial.matchXMaterial(split[0]);

                if (!material.isPresent()) {
                    continue;
                }

                bMap.put(
                        LocationSerializer.fromString(
                                blocks.replace(split[0] + ", ", "")
                        ),
                        material.get().parseItem()
                );
            }

        }

    }

    public String getName() {
        return name;
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

        runnable = new RegionRunnable(this);

        runnable.runTaskTimer(
                plugin,
                0L,
                Long.parseLong(
                        plugin.getConfigurationHandler(SlimeFile.BLOCKS).getString("regions." + name + ".update-delay")
                )
        );
    }

    public void pause() {
        cancel();
    }

    public void cancel() {
        if (runnable != null && !runnable.isCancelled()) {
            try {
                runnable.cancel();
            } catch (IllegalStateException ignored) {}
        }
    }

}
