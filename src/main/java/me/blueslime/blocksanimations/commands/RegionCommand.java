package me.blueslime.blocksanimations.commands;

import com.cryptomorin.xseries.XMaterial;
import dev.mruniverse.slimelib.commands.command.Command;
import dev.mruniverse.slimelib.commands.command.SlimeCommand;
import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import dev.mruniverse.slimelib.source.SlimeSource;
import dev.mruniverse.slimelib.source.player.SlimePlayer;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.regions.Region;
import me.blueslime.blocksanimations.regions.area.Cuboid;
import me.blueslime.blocksanimations.utils.LocationSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@Command(
        description = "Main Command of the plugin",
        shortDescription = "Main Command",
        usage = "/banimation (args)"
)
public class RegionCommand implements SlimeCommand {

    private final BlocksAnimations plugin;

    public RegionCommand(BlocksAnimations plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommand() {
        return "banimation";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(SlimeSource sender, String commandLabel, String[] args) {

        ConfigurationHandler messages = plugin.getMessages();

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (sender.hasPermission("blocksanimation.cmd.view")) {
                sender.sendColoredMessage(
                        "&aThis plugin was created by &bJustJustin&a with &lLove"
                );
            } else {
                sender.sendColoredMessage(
                        messages.getString("messages.plugin.permission-error", "&cYou don't have permissions to execute this command!")
                );
            }
            return;
        }

        if (!sender.hasPermission("blocksanimation.cmd.admin")) {
            sender.sendColoredMessage(
                    messages.getString("messages.plugin.permission-error", "&cYou don't have permissions to execute this command!")
            );
            return;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            sender.sendColoredMessage("&6/banimation animation-wand"); //Completed
            sender.sendColoredMessage("&eGet the Animation Wand Item to set pos1 and pos2");
            sender.sendColoredMessage("&6/banimation create (region-name)");
            sender.sendColoredMessage("&eCreate a region for an Animation");
            sender.sendColoredMessage("&6/banimation delete (region-name)");
            sender.sendColoredMessage("&eDelete a region using the name");
            sender.sendColoredMessage("&6/banimation area create (region-name)");
            sender.sendColoredMessage("&eCreate a new area for an specified region");
            sender.sendColoredMessage("&6/banimation area remove (region-name) (area-id)");
            sender.sendColoredMessage("&eRemove an area from a specified region");
            sender.sendColoredMessage("&6/banimation area edit (region-name) (area-id)");
            sender.sendColoredMessage("&eEdit an specified area-id for this region");
            sender.sendColoredMessage("&6/banimation start (region-name)"); //Completed
            sender.sendColoredMessage("&eStart a specified animation");
            sender.sendColoredMessage("&6/banimation pause (region-name)"); //Completed
            sender.sendColoredMessage("&ePause a specified animation");
            sender.sendColoredMessage("&6/banimation list"); //Completed
            sender.sendColoredMessage("&eCheck all Region List");
            sender.sendColoredMessage("&6/banimation reload"); //Completed
            sender.sendColoredMessage("&eReload all the plugin");
            return;
        }

        if (args[0].equalsIgnoreCase("animation-wand")) {
            if (sender.isConsoleSender()) {
                sender.sendColoredMessage("&cThis command is only for players");
                return;
            }
            sender.sendColoredMessage("&aNow you have the animation wand");

            ItemStack item = plugin.getListener().getItem();

            Player player = ((SlimePlayer)sender).get();

            player.getInventory().addItem(
                    item
            );
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 2) {
                sender.sendColoredMessage("&aArgument Usage:&b create (name)");
                return;
            }

            if (!sender.isPlayer()) {
                sender.sendColoredMessage("&cThis command is only for players!");
                return;
            }

            if (!plugin.getLocations().isCompleted(sender.getUniqueId())) {
                sender.sendColoredMessage("&6You need to set the pos1 and the pos2 for create animations!");
                return;
            }

            String region = args[1];

            if (blocks().contains("regions." + region + ".start-runnable-automatically")) {
                sender.sendColoredMessage("&cThis region is already created!");
                return;
            }

            blocks().set("regions." + region + ".start-runnable-automatically", true);
            blocks().set("regions." + region + ".update-delay", "20L");
            blocks().set("regions." + region + ".template-serializer", 1);
            blocks().set(
                    "regions." + region + ".cuboid.location-1",
                    LocationSerializer.toString(
                            plugin.getLocations().getFirstPosition(sender.getUniqueId()),
                            false
                    )
            );
            blocks().set(
                    "regions." + region + ".cuboid.location-2",
                    LocationSerializer.toString(
                            plugin.getLocations().getSecondPosition(sender.getUniqueId()),
                            false
                    )
            );
            blocks().save();
            blocks().reload();

            plugin.getStorage().getRegions().add(
                    region,
                    new Region(
                            plugin,
                            region
                    )
            );
            return;
        }

        if (args[0].equalsIgnoreCase("area")) {
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (!sender.isPlayer()) {
                        sender.sendColoredMessage("&cThis command is only for players!");
                        return;
                    }
                    String region = args[2];

                    if (blocks().contains("regions." + region + ".start-runnable-automatically")) {
                        int serializer = blocks().getInt("regions." + region + ".template-serializer", 1);

                        Cuboid cuboid = plugin.getStorage().getRegions().get(region).getCuboid();

                        ArrayList<String> blockList = new ArrayList<>();
                        ArrayList<Block> list = new ArrayList<>();

                        cuboid.iterator().forEachRemaining(list::add);

                        boolean includeData = XMaterial.supports(9);

                        for (Block block : list) {
                            if (block.getType() != Material.AIR) {
                                if (!includeData) {
                                    blockList.add(
                                            block.getType() + ":"  + block.getType().getId() + ", " +
                                                    LocationSerializer.toString(
                                                            block.getLocation(),
                                                            false
                                                    )
                                    );
                                } else {
                                    blockList.add(
                                            block.getType() + ", " +
                                                    LocationSerializer.toString(
                                                            block.getLocation(),
                                                            false
                                                    )
                                    );
                                }
                            }
                        }

                        blocks().set(
                                "regions." + region + "area-templates." + serializer,
                                blockList
                        );

                        blocks().set("regions." + region + ".template-serializer", serializer + 1);

                        blocks().save();

                        blocks().reload();
                        sender.sendColoredMessage(
                                messages.getString(
                                        "messages.area-template.create",
                                        "&aArea Template in region %id% has been created with the number %area-id%"
                                ).replace(
                                        "%id%", region
                                ).replace(
                                        "%area-id%",
                                        serializer + ""
                                ).replace(
                                        "%blocks%",
                                        blockList.size() + ""
                                )
                        );
                    }
                }
            }
            return;
        }


        if (args[0].equalsIgnoreCase("reload")) {
            long before = System.currentTimeMillis();

            plugin.reload();

            for (Region region : plugin.getStorage().getRegions().getValues()) {
                region.cancel();
            }

            plugin.getStorage().restart();

            long after = System.currentTimeMillis();

            sender.sendColoredMessage(
                    messages.getString("messages.plugin.reload", "&aThe plugin has been reloaded correctly in <ms>ms").replace("<ms>", (after - before) + "")
            );
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (plugin.getStorage().getRegions().getKeys().size() == 0) {
                sender.sendColoredMessage("&cYou don't have any animations created yet!");
                return;
            }
            for (Region region : plugin.getStorage().getRegions().getValues()) {
                String status;

                if (!region.getRunnable().isCancelled()) {
                    status = "&aRunning";
                } else {
                    status = "&cStopped";
                }

                sender.sendColoredMessage("&6- &e" + region.getName() + "&e, Status: " + status);
            }
            return;
        }

        if (args[0].equalsIgnoreCase("pause") && args.length >= 2) {
            Region region = plugin.getStorage().getRegions().get(args[1]);

            if (region == null) {
                sender.sendColoredMessage("&5This region doesn't exists");
                return;
            }

            region.pause();
            sender.sendColoredMessage(
                    messages.getString("messages.animation.paused", "&eAnimation %id% has been paused!").replace("%id%", args[1])
            );
            return;
        }

        if (args[0].equalsIgnoreCase("start") && args.length >= 2) {
            Region region = plugin.getStorage().getRegions().get(args[1]);

            if (region == null) {
                sender.sendColoredMessage("&5This region doesn't exists");
                return;
            }

            region.start();
            sender.sendColoredMessage(
                    messages.getString("messages.animation.started", "&eAnimation %id% has been started!").replace("%id%", args[1])
            );
        }


    }

    private ConfigurationHandler blocks() {
        return plugin.getConfigurationHandler(SlimeFile.BLOCKS);
    }
}
