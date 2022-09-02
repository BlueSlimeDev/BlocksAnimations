package me.blueslime.blocksanimations.listeners;

import com.cryptomorin.xseries.XMaterial;
import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.utils.LocationSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnimationWandListener implements Listener {
    private final BlocksAnimations plugin;

    private ItemStack item;


    public AnimationWandListener(BlocksAnimations plugin, ConfigurationHandler settings) {

        this.plugin = plugin;

        List<String> lore = new ArrayList<>();
        lore.add("&8BlocksAnimation Plugin");
        lore.add("&dBlocksAnimation Plugin");
        lore.add("&5BlocksAnimation Plugin");

        Optional<XMaterial> material = XMaterial.matchXMaterial(
                settings.getString("settings.animation-wand.item", "BEDROCK").toUpperCase()
        );

        if (material.isPresent()) {
            item = material.get().parseItem();
        } else {
            item = new ItemStack(Material.BEDROCK);
        }

        if (item == null){
            item = new ItemStack(Material.BEDROCK);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            settings.getString("settings.animation-wand.name", "&eBA &aAnimation Wand")
                    )
            );

            meta.setLore(
                    lore
            );

            item.setItemMeta(meta);
        }

        plugin.getServer().getPluginManager().registerEvents(
                this,
                plugin
        );
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        Block clickedBlock = event.getClickedBlock();

        Player player = event.getPlayer();

        if (clickedBlock == null) {
            return;
        }
        if (!player.hasPermission("blocksanimation.cmd.admin")) {
            return;
        }

        if (!isItem(player.getItemInHand())) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            plugin.getLocations().setFirstPosition(
                    player.getUniqueId(),
                    clickedBlock.getLocation()
            );

            int size = range(player.getUniqueId());

            String b;

            if (size == 1) {
                b = "block";
            } else {
                b = "blocks";
            }

            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            "&aPos1 now is set! (" +
                                    LocationSerializer.toString(clickedBlock.getLocation(), false) +
                                    ") (" + size + " " + b + ")"
                    )
            );
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getLocations().setSecondPosition(
                    player.getUniqueId(),
                    event.getClickedBlock().getLocation()
            );

            int size = range(player.getUniqueId());

            String b;

            if (size == 1) {
                b = "block";
            } else {
                b = "blocks";
            }

            player.sendMessage(
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            "&aPos2 now is set! (" +
                                    LocationSerializer.toString(clickedBlock.getLocation(), false) +
                                    ") (" + size + " " + b + ")"
                    )
            );
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getLocations().remove(
                event.getPlayer().getUniqueId()
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getLocations().remove(
                event.getPlayer().getUniqueId()
        );
    }

    private int range(UUID uuid) {
        return range(
                plugin.getLocations().getFirstPosition(uuid),
                plugin.getLocations().getSecondPosition(uuid)
        );
    }

    private int range(Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null) {
            return 0;
        }
        int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        int blocks = 0;

        for (int x = xMin; x <= xMax; ++x) {
            for (int y = yMin; y <= yMax; ++y) {
                for (int z = zMin; z <= zMax; ++z) {
                    blocks++;
                }
            }
        }

        return blocks;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isItem(ItemStack itemStack) {
        return item.isSimilar(itemStack);
    }
}
