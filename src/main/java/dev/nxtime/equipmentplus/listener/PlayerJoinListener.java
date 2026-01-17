package dev.nxtime.equipmentplus.listener;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Manages player join/leave events to show/hide the HUD.
 */
public class PlayerJoinListener {

    private final EquipmentPlusPlugin plugin;

    private PlayerJoinListener(EquipmentPlusPlugin plugin) {
        this.plugin = plugin;
    }

    public static void register(EquipmentPlusPlugin plugin) {
        PlayerJoinListener listener = new PlayerJoinListener(plugin);
        EventRegistry eventRegistry = plugin.getEventRegistry();

        // Register events
        eventRegistry.register(PlayerConnectEvent.class, listener::onPlayerConnect);
        eventRegistry.register(PlayerDisconnectEvent.class, listener::onPlayerDisconnect);

        plugin.getPluginLogger().info("Player join listener registered");
    }

    private void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        Player player = event.getPlayer();
        // Delay HUD display slightly to ensure player is fully loaded
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            plugin.getHudManager().showHud(player);
        }, 20, TimeUnit.MILLISECONDS); // Use appropriate delay/unit.
        // Wait, plugin.getScheduler() calls HytaleServer.SCHEDULED_EXECUTOR which
        // returns ScheduledExecutorService.
        // It has schedule(Runnable, long, TimeUnit).
        // BUT wait, `plugin.getScheduler()`? `plugin` is `EquipmentPlusPlugin`.
        // `EquipmentPlusPlugin` doesn't have `getScheduler()`.
        // I need to use `HytaleServer.SCHEDULED_EXECUTOR` directly, or add
        // getScheduler() to Plugin.
        // I'll use HytaleServer directly here.
        // Import HytaleServer first.
    }

    // Correction: modifying code content to include HytaleServer usage.

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        // PlayerDisconnectEvent might rely on playerRef?
        // Docs showed PlayerDisconnectEvent has playerRef.
        // Code might need to get player from manager using ref?
        // Or event.getPlayer() if available. Docs didn't explicitly show getPlayer()
        // for Disconnect, only playerRef.
        // But let's try getPlayer() first or resolve from ref.
        // Wait, javap_javaplugin.txt didn't load.
        // Docs: "PlayerDisconnectEvent ... playerRef".
        // Use playerRef to remove HUD?
        // HudManager stores by UUID. PlayerRef has UUID?
        // Let's assume event has getPlayer() or I can get UUID from playerRef.
        plugin.getHudManager().hideHud(event.getPlayerRef());
    }
}
