package dev.nxtime.equipmentplus;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.nxtime.equipmentplus.command.EquipmentPlusCommand;
import dev.nxtime.equipmentplus.hud.DurabilityHudManager;
import dev.nxtime.equipmentplus.listener.PlayerJoinListener;
import dev.nxtime.equipmentplus.task.HudUpdateTask;
import dev.nxtime.equipmentplus.util.PluginLogger;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
 * @version 0.1.4-alpha
 */
public class EquipmentPlusPlugin extends JavaPlugin {

    public EquipmentPlusPlugin(JavaPluginInit init) {
        super(init);
    }

    private static EquipmentPlusPlugin instance;
    private PluginLogger logger;
    private DurabilityHudManager hudManager;
    private EquipmentPlusConfig config;
    private HudUpdateTask hudUpdateTask;

    /** Pending throttled HUD update tasks per player */
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> pendingHudUpdates = new ConcurrentHashMap<>();

    @Override
    public void setup() {
        instance = this;
        logger = new PluginLogger(getLogger());
        config = new EquipmentPlusConfig(this);

        logger.info("EquipmentPlus v0.1.4-alpha initializing...");

        // Load configuration
        config.load();

        // Initialize HUD manager
        hudManager = new DurabilityHudManager(this);

        // Register listeners
        PlayerJoinListener.register(this);

        // Register commands
        getCommandRegistry().registerCommand(new EquipmentPlusCommand(this));

        // Register inventory change event for event-driven HUD updates
        registerInventoryChangeListener();

        // Start backup update task (reduced frequency since we now use events)
        hudUpdateTask = new HudUpdateTask(this);
        hudUpdateTask.start();

        logger.info("EquipmentPlus initialized successfully!");
    }

    /**
     * Registers the inventory change listener for event-driven HUD updates.
     * Uses throttling to prevent server overload during rapid inventory changes.
     */
    private void registerInventoryChangeListener() {
        getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, (event) -> {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            if (!config.isHudEnabled()) {
                return;
            }

            UUID uuid = player.getUuid();

            // Check if we have a HUD for this player
            if (!hudManager.hasHud(uuid)) {
                return;
            }

            // Throttled refresh: schedule update for 50ms later if not already pending
            ScheduledFuture<?> task = pendingHudUpdates.get(uuid);
            if (task == null || task.isDone()) {
                task = HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
                    try {
                        hudManager.updateHud(player);
                    } catch (Exception e) {
                        logger.debug("Failed to update HUD on inventory change: " + e.getMessage());
                    }
                }, 50, TimeUnit.MILLISECONDS);
                pendingHudUpdates.put(uuid, task);
            }
        });

        logger.debug("Registered LivingEntityInventoryChangeEvent for event-driven HUD updates");
    }

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
