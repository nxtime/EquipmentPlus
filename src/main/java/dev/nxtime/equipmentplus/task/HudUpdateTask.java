package dev.nxtime.equipmentplus.task;

import com.hypixel.hytale.server.core.HytaleServer;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Periodic task to update HUD durability values.
 */
public class HudUpdateTask implements Runnable {

    private final EquipmentPlusPlugin plugin;
    private ScheduledFuture<?> task;

    public HudUpdateTask(EquipmentPlusPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            task.cancel(false);
        }

        int interval = plugin.getPluginConfig().getHudUpdateTicks();
        long period = interval * 50L;

        task = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(this, period, period, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    @Override
    public void run() {
        try {
            if (plugin.getPluginConfig().isHudEnabled()) {
                plugin.getHudManager().updateAllHuds();
            }
        } catch (Exception e) {
            plugin.getPluginLogger().error("Error in HudUpdateTask: " + e.getMessage());
        }
    }
}
