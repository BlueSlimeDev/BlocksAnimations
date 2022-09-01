package me.blueslime.blocksanimations.loader;

import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;
import dev.mruniverse.slimelib.file.configuration.ConfigurationProvider;
import dev.mruniverse.slimelib.file.configuration.provider.BukkitConfigurationProvider;
import dev.mruniverse.slimelib.loader.BaseSlimeLoader;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import me.blueslime.blocksanimations.BlocksAnimations;
import me.blueslime.blocksanimations.utils.FileUtilities;
import me.blueslime.blocksanimations.SlimeFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginLoader extends BaseSlimeLoader<JavaPlugin> {

    private ConfigurationHandler messages = null;

    private final File langDirectory;

    public PluginLoader(BlocksAnimations plugin) {
        super(plugin);

        langDirectory = new File(
                plugin.getDataFolder(),
                "lang"
        );

        boolean loadDefaults = false;

        if (!langDirectory.exists()) {
            plugin.getLogs().info("Language directory has been verified (" + langDirectory.mkdirs() + ")");
            loadDefaults = true;
        }

        if (loadDefaults) {
            loadDefaults();
        }
    }

    public void init() {
        if (this.getFiles() != null) {
            this.getFiles().init();

            loadLang();
        }
    }

    private void loadLang() {
        String lang = getFiles().getConfigurationHandler(SlimeFile.SETTINGS).getString("settings.default-lang", "en");

        File messages = new File(
                langDirectory,
                lang + ".yml"
        );

        if (messages.exists()) {
            ConfigurationProvider provider = new BukkitConfigurationProvider();

            this.messages = provider.create(
                    getPlugin().getLogs(),
                    messages
            );

            getPlugin().getLogs().info("Messages are loaded from Lang file successfully.");
        } else {
            getPlugin().getLogs().error("Can't load messages correctly, debug will be showed after this message:");
            getPlugin().getLogs().debug("Language file of messages: " + messages.getAbsolutePath());
            getPlugin().getLogs().debug("Language name file of messages: " + messages.getName());
        }
    }

    private void loadDefaults() {
        SlimeLogs logs = getPlugin().getLogs();

        FileUtilities.load(
                logs,
                langDirectory,
                "en.yml",
                "/lang/en.yml"
        );

        FileUtilities.load(
                logs,
                langDirectory,
                "es.yml",
                "/lang/es.yml"
        );
    }

    public ConfigurationHandler getMessages() {
        return messages;
    }

    public void shutdown() {
        this.getCommands().unregister();
    }

    public void reload() {
        this.getFiles().reloadFiles();
        this.loadLang();
    }

}
