package me.blueslime.blocksanimations;

import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimePluginInformation;
import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import dev.mruniverse.slimelib.logs.SlimeLogger;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import me.blueslime.blocksanimations.exceptions.NotFoundLanguageException;
import me.blueslime.blocksanimations.listeners.AnimationWandListener;
import me.blueslime.blocksanimations.loader.PluginLoader;
import me.blueslime.blocksanimations.loader.PluginLoaderDelay;
import me.blueslime.blocksanimations.storage.LocationStorage;
import me.blueslime.blocksanimations.storage.RegionStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class BlocksAnimations extends JavaPlugin implements SlimePlugin<JavaPlugin> {

    private SlimePluginInformation information;

    private AnimationWandListener animationLs;

    private LocationStorage locationStorage;

    private RegionStorage regionStorage;

    private PluginLoader loader;

    private SlimeLogs logs;

    public void onEnable() {
        this.logs = SlimeLogger.createLogs(
                getServerType(),
                this
        );

        this.information = new SlimePluginInformation(
                getServerType(),
                this
        );

        this.loader = new PluginLoader(this);

        new PluginLoaderDelay(this).runTaskLater(
                this,
                1L
        );
    }

    public void loadRegions() {
        this.regionStorage = new RegionStorage(this);

        this.animationLs = new AnimationWandListener(
                getConfigurationHandler(SlimeFile.SETTINGS)
        );

        this.locationStorage = new LocationStorage();
    }

    public ConfigurationHandler getMessages() {
        ConfigurationHandler configuration = getLoader().getMessages();

        if (configuration == null) {
            exception();
        }

        return configuration;
    }

    private void exception() {
        new NotFoundLanguageException("The current language in the settings file doesn't exists, probably you will see errors in console").printStackTrace();
    }

    public RegionStorage getStorage() {
        return regionStorage;
    }

    public LocationStorage getLocations() {
        return locationStorage;
    }

    public AnimationWandListener getListener() {
        return animationLs;
    }

    @Override
    public SlimePluginInformation getPluginInformation() {
        return information;
    }

    @Override
    public PluginLoader getLoader() {
        return loader;
    }

    @Override
    public SlimeLogs getLogs() {
        return logs;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public void reload() {
        loader.reload();
    }
}