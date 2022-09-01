package me.blueslime.blocksanimations.loader;

import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.SlimeFile;
import me.blueslime.blocksanimations.commands.RegionCommand;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginLoaderDelay extends BukkitRunnable {

    private final BlocksAnimations main;

    public PluginLoaderDelay(BlocksAnimations main) {
        this.main = main;
    }

    @Override
    public void run() {
        main.getLoader().setFiles(SlimeFile.class);

        main.getLoader().init();

        main.loadRegions();

        main.getCommands().register(new RegionCommand(main));
    }
}
