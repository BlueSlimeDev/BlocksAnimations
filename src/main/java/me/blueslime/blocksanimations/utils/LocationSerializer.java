package me.blueslime.blocksanimations.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.util.logging.Level;

public class LocationSerializer {

    private static final DecimalFormat format = new DecimalFormat("0.00");

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public static String toDecimal(double value) {
        return decimalFormat.format(value);
    }

    public static Location fromString(String location) {
        if (location == null) {
            return null;
        }

        String[] text = location.replace(" ", "").split(",");

        if (text.length != 6) {
            if (text.length == 4) {
                double x = Double.parseDouble(text[1]);
                double y = Double.parseDouble(text[2]);
                double z = Double.parseDouble(text[3]);

                World world = Bukkit.getWorld(text[0]);

                if (world == null) {
                    Bukkit.getLogger().log(Level.INFO, "The plugin can't get a specified world because was detected as null");
                }
                return new Location(
                        world,
                        x,
                        y,
                        z
                );
            }
            return null;
        }

        double x = Double.parseDouble(text[1]);
        double y = Double.parseDouble(text[2]);
        double z = Double.parseDouble(text[3]);
        double yaw = Double.parseDouble(text[4]);
        double pitch = Double.parseDouble(text[5]);

        World world = Bukkit.getWorld(text[0]);

        if (world == null) {
            Bukkit.getLogger().log(Level.INFO, "The plugin can't get a specified world because was detected as null");
        }

        Location loc = new Location(
                world,
                x,
                y,
                z
        );

        loc.setYaw(
                (float)yaw
        );

        loc.setPitch(
                (float)pitch
        );

        return loc;
    }

    public static String toString(Location location, boolean includeExtras) {
        if (location.getWorld() == null) {
            if (includeExtras) {
                return "World, " +
                        format.format(location.getX()) + ", " +
                        format.format(location.getY()) + ", " +
                        format.format(location.getZ()) + ", " +
                        location.getYaw() + ", " +
                        location.getPitch();
            } else {
                return "World, " +
                        format.format(location.getX()) + ", " +
                        format.format(location.getY()) + ", " +
                        format.format(location.getZ());
            }
        }
        if (includeExtras) {
            return location.getWorld().getName() + ", " +
                    format.format(location.getX()) + ", " +
                    format.format(location.getY()) + ", " +
                    format.format(location.getZ()) + ", " +
                    location.getYaw() + ", " +
                    location.getPitch();
        }
        return location.getWorld().getName() + ", " +
                format.format(location.getX()) + ", " +
                format.format(location.getY()) + ", " +
                format.format(location.getZ());
    }

    public static String toString(Location location) {
        return toString(location, true);
    }

}


