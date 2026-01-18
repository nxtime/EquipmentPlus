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
        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            plugin.getHudManager().showHud(player);
        }, 20, TimeUnit.MILLISECONDS);
    }

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        plugin.getHudManager().hideHud(event.getPlayerRef());
    }
}
