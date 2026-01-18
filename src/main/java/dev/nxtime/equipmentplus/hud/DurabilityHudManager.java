package dev.nxtime.equipmentplus.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the armor durability HUD for all players.
 * Integrates with MultipleHUD if available, otherwise uses native HUD system.
 */
public class DurabilityHudManager {
    private static final String HUD_FILE = "Hud/dev.nxtime_EquipmentPlus_DurabilityHud.ui";

    private final EquipmentPlusPlugin plugin;
    private final Map<UUID, DurabilityHud> playerHuds = new ConcurrentHashMap<>();
    private boolean multipleHudAvailable = false;
    private volatile Anchor rootAnchor;
    private Class<?> multiHudClass;
    private static volatile Method getInstanceMethod;
    private static volatile Method setCustomHudMethod;
    private static volatile Method hideCustomHudMethod;

    public DurabilityHudManager(EquipmentPlusPlugin plugin) {
        this.plugin = plugin;
        detectMultipleHud();
    }

    /**
     * Detects if MultipleHUD mod is available.
     */
    private void detectMultipleHud() {
        try {
            this.multiHudClass = this.loadMultiHudClass();
            if (this.multiHudClass == null) {
                throw new ClassNotFoundException("com.buuz135.mhud.MultipleHUD");
            }

            // Initialize method references during detection
            getInstanceMethod = this.multiHudClass.getMethod("getInstance");
            setCustomHudMethod = this.multiHudClass.getMethod("setCustomHud", Player.class, PlayerRef.class,
                    String.class, CustomUIHud.class);
            hideCustomHudMethod = this.multiHudClass.getMethod("hideCustomHud", Player.class, PlayerRef.class,
                    String.class);

            multipleHudAvailable = true;
            plugin.getPluginLogger().info("MultipleHUD detected - using integrated HUD system");
        } catch (Exception e) {
            multipleHudAvailable = false;
            this.multiHudClass = null;
            getInstanceMethod = null;
            setCustomHudMethod = null;
            hideCustomHudMethod = null;
            plugin.getPluginLogger().info("MultipleHUD not found - using native HUD system");
        }
    }

    /**
     * Shows the durability HUD for a player.
     */
    public void showHud(Player player) {
        if (!plugin.getPluginConfig().isHudEnabled()) {
            return;
        }

        UUID uuid = player.getUuid();

        if (!playerHuds.containsKey(uuid)) {
            DurabilityHud hud = new DurabilityHud(plugin, player);
            playerHuds.put(uuid, hud);

            if (multipleHudAvailable) {
                registerWithMultipleHud(player, hud);
            } else {
                registerNativeHud(player, hud);
            }

            // Mark the HUD as visible and trigger build() AFTER registration
            hud.show();

            // Initial data will be set in build() method using default field values
            // Don't call update() here - let it happen naturally on the next tick
        }
    }

    /**
     * Hides the durability HUD for a player.
     */
    public void hideHud(Player player) {
        UUID uuid = player.getUuid();
        DurabilityHud hud = playerHuds.remove(uuid);
        if (hud != null) {
            hud.hide(); // stop updates
            if (multipleHudAvailable) {
                unregisterFromMultipleHud(player);
            } else {
                unregisterNativeHud(player);
            }
        }
    }

    /**
     * Removes the HUD from management for a player reference (e.g. on disconnect).
     */
    public void hideHud(PlayerRef playerRef) {
        if (playerRef != null) {
            playerHuds.remove(playerRef.getUuid());
        }
    }

    /**
     * Updates the HUD for a specific player.
     */
    public void updateHud(Player player) {
        UUID uuid = player.getUuid();
        DurabilityHud hud = playerHuds.get(uuid);
        if (hud != null) {
            hud.update();
        }
    }

    /**
     * Updates all active HUDs (called on tick).
     */
    public void updateAllHuds() {
        for (DurabilityHud hud : playerHuds.values()) {
            hud.update();
        }
    }

    /**
     * Cleanup all HUDs.
     */
    public void cleanup() {
        for (DurabilityHud hud : playerHuds.values()) {
            hud.hide(); // stop updates immediately
            if (multipleHudAvailable) {
                unregisterFromMultipleHud(hud.getPlayer());
            } else {
                unregisterNativeHud(hud.getPlayer());
            }
        }
        playerHuds.clear();
    }

    private static Class<?> loadMultiHudClass() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return Class.forName("com.buuz135.mhud.MultipleHUD", true, cl);
            }
        } catch (Throwable var3) {
        }

        try {
            PluginManager pm = PluginManager.get();
            if (pm != null && pm.getBridgeClassLoader() != null) {
                return pm.getBridgeClassLoader().loadClass("com.buuz135.mhud.MultipleHUD");
            }
        } catch (Throwable var2) {
        }

        try {
            return Class.forName("com.buuz135.mhud.MultipleHUD");
        } catch (Throwable var1) {
            return null;
        }
    }

    /**
     * Register HUD with MultipleHUD mod.
     */
    private void registerWithMultipleHud(Player player, DurabilityHud hud) {
        try {
            // Check if all required method references are initialized
            if (getInstanceMethod == null || setCustomHudMethod == null) {
                plugin.getPluginLogger().warn("MultipleHUD method references not initialized");
                return;
            }

            // Get the MultipleHUD instance
            Object instance = getInstanceMethod.invoke(null);

            // Critical: Check if instance is null before using it
            if (instance == null) {
                plugin.getPluginLogger().warn("MultipleHUD getInstance() returned null");
                return;
            }

            // Register the custom HUD
            setCustomHudMethod.invoke(instance, player, player.getPlayerRef(), HUD_FILE, hud.getCustomHud());
            plugin.getPluginLogger()
                    .debug("Successfully registered HUD with MultipleHUD for player: " + player.getUuid());
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to register with MultipleHUD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Unregister HUD from MultipleHUD mod.
     */
    private void unregisterFromMultipleHud(Player player) {
        try {
            // Check if all required method references are initialized
            if (getInstanceMethod == null || hideCustomHudMethod == null) {
                plugin.getPluginLogger().warn("MultipleHUD method references not initialized for hide");
                return;
            }

            // Get the MultipleHUD instance
            Object instance = getInstanceMethod.invoke(null);

            // Critical: Check if instance is null before using it
            if (instance == null) {
                plugin.getPluginLogger().warn("MultipleHUD getInstance() returned null during hide");
                return;
            }

            // Unregister the custom HUD
            hideCustomHudMethod.invoke(instance, player, player.getPlayerRef(), HUD_FILE);
            plugin.getPluginLogger()
                    .debug("Successfully unregistered HUD from MultipleHUD for player: " + player.getUuid());
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to unregister from MultipleHUD: " + e.getMessage());
        }
    }

    /**
     * Unregisters HUD from the native Hytale HUD system.
     */
    private void unregisterNativeHud(Player player) {
        try {
            if (player == null) {
                plugin.getPluginLogger().debug("Cannot unregister native HUD: player is null");
                return;
            }

            HudManager hudManager = player.getHudManager();
            if (hudManager == null) {
                plugin.getPluginLogger().debug("Cannot unregister native HUD: hudManager is null");
                return;
            }

            // Only unregister if there's actually a custom HUD set
            CustomUIHud currentHud = hudManager.getCustomHud();
            if (currentHud != null) {
                hudManager.setCustomHud(player.getPlayerRef(), null);
                plugin.getPluginLogger().debug("Successfully unregistered native HUD for player: " + player.getUuid());
            }
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to unregister native HUD: " + e.getMessage());
        }
    }

    /**
     * Register HUD with native Hytale HUD system.
     */
    private void registerNativeHud(Player player, DurabilityHud hud) {
        try {
            HudManager hudManager = player.getHudManager();

            if (hudManager != null) {
                hudManager.setCustomHud(player.getPlayerRef(), hud.getCustomHud());
                plugin.getPluginLogger().debug("Successfully registered native HUD for player: " + player.getUuid());
            }
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to register native HUD: " + e.getMessage());
        }
    }

    public boolean isMultipleHudAvailable() {
        return multipleHudAvailable;
    }
}
