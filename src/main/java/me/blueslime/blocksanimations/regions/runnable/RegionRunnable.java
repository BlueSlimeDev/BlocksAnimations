package me.blueslime.blocksanimations.regions.runnable;

import me.blueslime.blocksanimations.regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class RegionRunnable extends BukkitRunnable {

    private final ArrayList<Block> blockMap = new ArrayList<>();
    private final Region region;

    private int current;

    public RegionRunnable(Region region) {
        this.region = region;

        region.getCuboid().iterator().forEachRemaining(blockMap::add);
    }

    @Override
    public void run() {
        if (region.getBlockMap().size() <= 1) {
            cancel();
        }

        if (current == region.getBlockMap().size()) {
            current = 1;
        }

        Map<Location, ItemStack> currentMap = region.getBlockMap().get(current);

        if (currentMap == null || currentMap.isEmpty()) {
            current++;
            return;
        }

        for (Block block : blockMap) {
            if (currentMap.containsKey(block.getLocation())) {

                block.setType(
                        currentMap.get(block.getLocation()).getType()
                );

            } else {
                block.setType(Material.AIR);
            }
        }

        current++;
    }
}
