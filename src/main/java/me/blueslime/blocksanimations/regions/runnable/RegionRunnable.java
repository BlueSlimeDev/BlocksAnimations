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
    private final boolean debug;

    private int current;

    public RegionRunnable(Region region, boolean debug) {
        this.region = region;
        this.debug  = debug;
        blockMap.addAll(region.getCuboid().blocks());
    }

    @Override
    public void run() {
        if (region.getBlockMap().size() <= 1) {
            if (debug) {
                region.getLogs().debug("cancelling animation because only have one area created or don't have areas created yet");
            }
            cancel();
        }

        if (current >= region.getBlockMap().size()) {
            if (debug) {
                region.getLogs().debug("Restarted animation");
            }
            current = 1;
        }


        Map<Location, ItemStack> currentMap = region.getBlockMap().get(current);

        if (currentMap == null || currentMap.isEmpty()) {
            current++;
            if (debug) {
                region.getLogs().debug("0 Blocks detected in a specified area id: " + current);
            }
            return;
        }

        for (Block block : blockMap) {
            if (debug) {
                region.getLogs().debug(
                        "For Location: " + block.getLocation() + " checking for animation block map"
                );
            }
            if (currentMap.containsKey(block.getLocation())) {

                block.setType(
                        currentMap.get(block.getLocation()).getType()
                );

                block.getState().update();

                if (debug) {
                    region.getLogs().debug(
                            "Block detected in location: " + block.getLocation()
                    );
                }
            } else {
                block.setType(Material.AIR);

                block.getState().update();
            }
        }

        current++;
    }
}
