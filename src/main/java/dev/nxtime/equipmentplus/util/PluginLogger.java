package dev.nxtime.equipmentplus.util;

import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Level;

/**
 * Simple logger wrapper for consistent plugin logging.
 */
public class PluginLogger {

    private final HytaleLogger logger;

    public PluginLogger(HytaleLogger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        logger.at(Level.INFO).log(message);
    }

    public void warn(String message) {
        logger.at(Level.WARNING).log(message);
    }

    public void error(String message) {
        logger.at(Level.SEVERE).log(message);
    }

    public void debug(String message) {
        // Only log debug if level is suitable, or always log and let logger handle it
        logger.at(Level.FINE).log(message);
    }
}
