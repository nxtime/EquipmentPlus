package dev.nxtime.equipmentplus;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.nxtime.equipmentplus.command.EquipmentPlusCommand;
import dev.nxtime.equipmentplus.hud.DurabilityHudManager;
import dev.nxtime.equipmentplus.listener.PlayerJoinListener;
import dev.nxtime.equipmentplus.task.HudUpdateTask;
import dev.nxtime.equipmentplus.util.PluginLogger;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * EquipmentPlus - Armor durability HUD and auto-repair functionality.
 * <p>
 * Features:
 * <ul>
 * <li>Armor durability HUD (left/right configurable)</li>
 * <li>Auto-repair when items break if repair kit in inventory</li>
 * </ul>
 *
 * @author nxtime
 * @version 0.1.0-alpha
 */
public class EquipmentPlusPlugin extends JavaPlugin {

    public EquipmentPlusPlugin(JavaPluginInit init) {
        super(init);
    }

    private static EquipmentPlusPlugin instance;
    private PluginLogger logger;
    private DurabilityHudManager hudManager;
    private EquipmentPlusConfig config;

    @Override
    public void setup() {
        instance = this;
        logger = new PluginLogger(getLogger());
        config = new EquipmentPlusConfig(this);

        logger.info("EquipmentPlus v0.1.0-alpha initializing...");

        // Load configuration
        config.load();

        // Initialize HUD manager
        hudManager = new DurabilityHudManager(this);

        // Register listeners
        // ItemBreakListener.register(this);
        PlayerJoinListener.register(this);

        // Register commands
        getCommandRegistry().registerCommand(new EquipmentPlusCommand(this));

        // Start update task
        hudUpdateTask = new HudUpdateTask(this);
        hudUpdateTask.start();

        logger.info("EquipmentPlus initialized successfully!");
    }

    private HudUpdateTask hudUpdateTask;

    @Override
    public void shutdown() {
        logger.info("EquipmentPlus shutting down...");

        if (hudManager != null) {
            hudManager.cleanup();
        }

        if (hudUpdateTask != null) {
            hudUpdateTask.stop();
        }

        config.save();
        instance = null;
    }

    public static EquipmentPlusPlugin getInstance() {
        return instance;
    }

    public PluginLogger getPluginLogger() {
        return logger;
    }

    public DurabilityHudManager getHudManager() {
        return hudManager;
    }

    public EquipmentPlusConfig getPluginConfig() {
        return config;
    }
}
