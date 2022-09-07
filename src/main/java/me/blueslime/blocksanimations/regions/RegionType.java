package me.blueslime.blocksanimations.regions;

import dev.mruniverse.slimelib.file.configuration.ConfigurationHandler;

import java.util.Locale;

public enum RegionType {
    INTERACT,
    DEFAULT;

    public static RegionType fromString(String text) {
        switch (text.toLowerCase(Locale.ENGLISH)) {
            case "1":
            case "interact":
            case "on_interact":
            case "on-interact":
                return INTERACT;
            case "0":
            case "default":
            default:
                return DEFAULT;
        }
    }

    public static RegionType fromConfiguration(ConfigurationHandler handler, String path) {
        return fromString(
                handler.getString(
                        path,
                        "DEFAULT"
                )
        );
    }
}
