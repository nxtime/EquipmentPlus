package dev.nxtime.equipmentplus.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.nxtime.equipmentplus.EquipmentPlusConfig;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;

import javax.annotation.Nonnull;

/**
 * Interactive GUI page for managing EquipmentPlus settings.
 */
public class EquipmentPlusGuiPage extends InteractiveCustomUIPage<EquipmentPlusGuiPage.GuiData> {

    public EquipmentPlusGuiPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, GuiData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder,
            @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        // Load the UI file
        uiCommandBuilder.append("Pages/dev.nxtime_EquipmentPlus_Menu.ui");

        EquipmentPlusConfig config = EquipmentPlusPlugin.getInstance().getPluginConfig();

        // Bind initial values
        uiCommandBuilder.set("#HudEnabledSetting #CheckBox.Value", config.isHudEnabled());
        uiCommandBuilder.set("#HudPositionSetting #CheckBox.Value", "right".equalsIgnoreCase(config.getHudPosition()));

        // Register event handlers
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HudEnabledSetting #CheckBox",
                EventData.of("Button", "ToggleHud"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HudPositionSetting #CheckBox",
                EventData.of("Button", "TogglePosition"), false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store,
            @Nonnull GuiData data) {
        super.handleDataEvent(ref, store, data);

        Player player = store.getComponent(ref, Player.getComponentType());
        EquipmentPlusPlugin plugin = EquipmentPlusPlugin.getInstance();
        EquipmentPlusConfig config = plugin.getPluginConfig();

        if (data.button != null) {
            switch (data.button) {
                case "ToggleHud" -> {
                    boolean newState = !config.isHudEnabled();
                    config.setHudEnabled(newState);
                    if (newState) {
                        plugin.getHudManager().showHud(player);
                    } else {
                        plugin.getHudManager().hideHud(player);
                    }
                }
                case "TogglePosition" -> {
                    String current = config.getHudPosition();
                    String newPos = "left".equalsIgnoreCase(current) ? "right" : "left";
                    config.setHudPosition(newPos);
                    // Refresh HUD for player to apply new anchor
                    plugin.getHudManager().hideHud(player);
                    plugin.getHudManager().showHud(player);
                }
            }
        }

        // Update the UI to reflect new state
        this.sendUpdate();
    }

    /**
     * Data model for GUI events.
     */
    public static class GuiData {
        static final String KEY_BUTTON = "Button";

        public static final BuilderCodec<GuiData> CODEC = BuilderCodec
                .<GuiData>builder(GuiData.class, GuiData::new)
                .addField(new KeyedCodec<>(KEY_BUTTON, Codec.STRING),
                        (data, s) -> data.button = s,
                        data -> data.button)
                .build();

        private String button;
    }
}
