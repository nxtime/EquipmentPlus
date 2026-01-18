package dev.nxtime.equipmentplus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Configuration manager for EquipmentPlus.
 */
public class EquipmentPlusConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final JavaPlugin plugin;
    private final Path configPath;

    // Configuration values
    private boolean hudEnabled = true;
    private String hudPosition = "left"; // "left" or "right"
    private boolean autoRepairEnabled = true;
    private int hudUpdateTicks = 200; // Backup update every 10 seconds (events handle most updates)

    public EquipmentPlusConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configPath = plugin.getDataDirectory().resolve("config.json");
    }

    public void load() {
        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    this.hudEnabled = data.hudEnabled;
                    this.hudPosition = data.hudPosition != null ? data.hudPosition : "left";
                    this.autoRepairEnabled = data.autoRepairEnabled;
                    this.hudUpdateTicks = data.hudUpdateTicks > 0 ? data.hudUpdateTicks : 20;
                }
            }
        } catch (IOException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to load config: " + e.getMessage());
        }
    }

    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            ConfigData data = new ConfigData();
            data.hudEnabled = this.hudEnabled;
            data.hudPosition = this.hudPosition;
            data.autoRepairEnabled = this.autoRepairEnabled;
            data.hudUpdateTicks = this.hudUpdateTicks;
            Files.writeString(configPath, GSON.toJson(data));
        } catch (IOException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to save config: " + e.getMessage());
        }
    }

    // Getters and setters
    public boolean isHudEnabled() {
        return hudEnabled;
    }

    public void setHudEnabled(boolean enabled) {
        this.hudEnabled = enabled;
        save();
    }

    public String getHudPosition() {
        return hudPosition;
    }

    public void setHudPosition(String position) {
        this.hudPosition = position;
        save();
    }

    public boolean isAutoRepairEnabled() {
        return autoRepairEnabled;
    }

    public void setAutoRepairEnabled(boolean enabled) {
        this.autoRepairEnabled = enabled;
        save();
    }

    public int getHudUpdateTicks() {
        return hudUpdateTicks;
    }

    public void setHudUpdateTicks(int ticks) {
        this.hudUpdateTicks = Math.max(1, ticks);
        save();
    }

    /**
     * Internal data class for JSON serialization.
     */
    private static class ConfigData {
        boolean hudEnabled = true;
        String hudPosition = "left";
        boolean autoRepairEnabled = true;
        int hudUpdateTicks = 20;
    }
}
