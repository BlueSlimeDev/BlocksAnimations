package me.blueslime.blocksanimations.utils;

import dev.mruniverse.slimelib.logs.SlimeLogs;

public class LoaderUtils {

    public static void setupLogs(SlimeLogs logs) {
        logs.getSlimeLogger().setHidePackage("me.blueslime.blocksanimations.");
        logs.getSlimeLogger().setContainIdentifier("me.blueslime.blocksanimations");
        logs.getSlimeLogger().setPluginName("BlocksAnimations");

        logs.getProperties().getExceptionProperties().BASE_COLOR = "&e";

        logs.getPrefixes().getIssue().setPrefix("&cBlocksAnimations | &7");
        logs.getPrefixes().getWarn().setPrefix("&eBlocksAnimations | &7");
        logs.getPrefixes().getDebug().setPrefix("&bBlocksAnimations | &7");
        logs.getPrefixes().getInfo().setPrefix("&6BlocksAnimations | &7");
    }
}