package dev.nxtime.equipmentplus.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;
import dev.nxtime.equipmentplus.util.UiUtils;

import javax.annotation.Nonnull;

/**
 * Individual durability HUD instance for a player.
 * Displays armor icons with durability percentages.
 */
public class DurabilityHud extends CustomUIHud {

    private static final String UI_PATH = "Hud/DurabilityHud.ui";
    private static final String UI_CONTENT = UiUtils.loadUiFile(DurabilityHud.class, UI_PATH);

    // Armor slot indices
    private static final int SLOT_HEAD = 0;
    private static final int SLOT_CHEST = 1;
    private static final int SLOT_HANDS = 2;
    private static final int SLOT_LEGS = 3;

    private final EquipmentPlusPlugin plugin;
    private final Player player;
    private boolean visible = false;

    private volatile boolean headVisible = false;
    private volatile boolean chestVisible = false;
    private volatile boolean handsVisible = false;
    private volatile boolean legsVisible = false;

    // Item data for each slot
    private volatile String headItemId = "";
    private volatile String headDurabilityText = "100%";
    private volatile double headDurabilityValue = 1.0;
    private volatile String headBarColor = "#00FF00";

    private volatile String chestItemId = "";
    private volatile String chestDurabilityText = "100%";
    private volatile double chestDurabilityValue = 1.0;
    private volatile String chestBarColor = "#00FF00";

    private volatile String handsItemId = "";
    private volatile String handsDurabilityText = "100%";
    private volatile double handsDurabilityValue = 1.0;
    private volatile String handsBarColor = "#00FF00";

    private volatile String legsItemId = "";
    private volatile String legsDurabilityText = "100%";
    private volatile double legsDurabilityValue = 1.0;
    private volatile String legsBarColor = "#00FF00";

    // Ammo slot
    private volatile boolean ammoVisible = false;
    private volatile String ammoItemId = "";
    private volatile String ammoRemainingText = "0";

    public DurabilityHud(EquipmentPlusPlugin plugin, Player player) {
        super(player.getPlayerRef());
        this.plugin = plugin;
        this.player = player;

        // Initialize state with current inventory data BEFORE build() is called
        try {
            ItemContainer armor = player.getInventory().getArmor();
            updateSlotState(armor.getItemStack((short) SLOT_HEAD), 0);
            updateSlotState(armor.getItemStack((short) SLOT_CHEST), 1);
            updateSlotState(armor.getItemStack((short) SLOT_HANDS), 2);
            updateSlotState(armor.getItemStack((short) SLOT_LEGS), 3);
            // Initialize ammo state
            updateAmmoState();
        } catch (Exception e) {
            // If initialization fails, use default values
            plugin.getPluginLogger().debug("Failed to initialize HUD state: " + e.getMessage());
        }
    }

    /**
     * Updates the HUD with current armor durability values.
     */
    public void update() {
        if (!visible) {
            return;
        }
        try {
            ItemContainer armor = player.getInventory().getArmor();

            // Update head slot state
            updateSlotState(armor.getItemStack((short) SLOT_HEAD), 0);
            // Update chest slot state
            updateSlotState(armor.getItemStack((short) SLOT_CHEST), 1);
            // Update hands slot state
            updateSlotState(armor.getItemStack((short) SLOT_HANDS), 2);
            // Update legs slot state
            updateSlotState(armor.getItemStack((short) SLOT_LEGS), 3);

            // Update ammo state
            updateAmmoState();

            // Build UI commands with updated state
            UICommandBuilder builder = new UICommandBuilder();
            buildSlotCommands(builder);

            // Send incremental update (false = don't rebuild entire UI)
            super.update(false, builder);
        } catch (Exception e) {
            plugin.getPluginLogger().debug("Failed to update HUD: " + e.getMessage());
        }
    }

    /**
     * Builds UI commands for all slots based on current field values.
     */
    private void buildSlotCommands(UICommandBuilder builder) {
        // Head slot
        builder.set("#HeadSlotGroup.Visible", headVisible);
        if (headVisible) {
            builder.set("#HeadItemIcon.ItemId", headItemId);
            builder.set("#HeadDurabilityText.Text", headDurabilityText);
            builder.set("#HeadDurabilityBar.Value", headDurabilityValue);
            PatchStyle headBarStyle = new PatchStyle();
            headBarStyle.setColor(Value.of(headBarColor));
            builder.setObject("#HeadDurabilityBar.Bar", headBarStyle);
        }

        // Chest slot
        builder.set("#ChestSlotGroup.Visible", chestVisible);
        if (chestVisible) {
            builder.set("#ChestItemIcon.ItemId", chestItemId);
            builder.set("#ChestDurabilityText.Text", chestDurabilityText);
            builder.set("#ChestDurabilityBar.Value", chestDurabilityValue);
            PatchStyle chestBarStyle = new PatchStyle();
            chestBarStyle.setColor(Value.of(chestBarColor));
            builder.setObject("#ChestDurabilityBar.Bar", chestBarStyle);
        }

        // Hands slot
        builder.set("#HandsSlotGroup.Visible", handsVisible);
        if (handsVisible) {
            builder.set("#HandsItemIcon.ItemId", handsItemId);
            builder.set("#HandsDurabilityText.Text", handsDurabilityText);
            builder.set("#HandsDurabilityBar.Value", handsDurabilityValue);
            PatchStyle handsBarStyle = new PatchStyle();
            handsBarStyle.setColor(Value.of(handsBarColor));
            builder.setObject("#HandsDurabilityBar.Bar", handsBarStyle);
        }

        // Legs slot
        builder.set("#LegsSlotGroup.Visible", legsVisible);
        if (legsVisible) {
            builder.set("#LegsItemIcon.ItemId", legsItemId);
            builder.set("#LegsDurabilityText.Text", legsDurabilityText);
            builder.set("#LegsDurabilityBar.Value", legsDurabilityValue);
            PatchStyle legsBarStyle = new PatchStyle();
            legsBarStyle.setColor(Value.of(legsBarColor));
            builder.setObject("#LegsDurabilityBar.Bar", legsBarStyle);
        }

        // Ammo slot
        builder.set("#AmmoSlotGroup.Visible", ammoVisible);
        if (ammoVisible) {
            builder.set("#AmmoItemIcon.ItemId", ammoItemId);
            builder.set("#AmmoRemainigText.Text", ammoRemainingText);
        }
    }

    /**
     * Updates the state fields for a single armor slot.
     */
    private void updateSlotState(ItemStack item, int slotIndex) {
        boolean hasItem = (item != null && item.getMaxDurability() > 0);

        if (hasItem) {
            double durability = item.getDurability();
            double maxDurability = item.getMaxDurability();
            int percent = (int) (durability / maxDurability * 100);
            double barValue = durability / maxDurability;
            String colorHex = getDurabilityColor(percent);
            String text = percent + "%";
            String itemId = item.getItemId();

            switch (slotIndex) {
                case 0: // Head
                    headVisible = true;
                    headItemId = itemId;
                    headDurabilityText = text;
                    headDurabilityValue = barValue;
                    headBarColor = colorHex;
                    break;
                case 1: // Chest
                    chestVisible = true;
                    chestItemId = itemId;
                    chestDurabilityText = text;
                    chestDurabilityValue = barValue;
                    chestBarColor = colorHex;
                    break;
                case 2: // Hands
                    handsVisible = true;
                    handsItemId = itemId;
                    handsDurabilityText = text;
                    handsDurabilityValue = barValue;
                    handsBarColor = colorHex;
                    break;
                case 3: // Legs
                    legsVisible = true;
                    legsItemId = itemId;
                    legsDurabilityText = text;
                    legsDurabilityValue = barValue;
                    legsBarColor = colorHex;
                    break;
            }
        } else {
            // Hide the slot
            switch (slotIndex) {
                case 0:
                    headVisible = false;
                    break;
                case 1:
                    chestVisible = false;
                    break;
                case 2:
                    handsVisible = false;
                    break;
                case 3:
                    legsVisible = false;
                    break;
            }
        }
    }

    /**
     * Updates the ammo state by checking player's inventory for arrows.
     */
    private void updateAmmoState() {
        try {
            ItemContainer inventory = player.getInventory().getCombinedEverything();
            int totalArrows = 0;
            String arrowItemId = null;

            // Search inventory for arrows
            short capacity = inventory.getCapacity();
            for (short i = 0; i < capacity; i++) {
                ItemStack item = inventory.getItemStack(i);
                if (item != null && !item.isEmpty()) {
                    String itemId = item.getItemId();
                    // Check if item is an arrow (you may need to adjust this check based on actual
                    // arrow item IDs)
                    if (itemId != null && (itemId.contains("arrow") || itemId.contains("Arrow"))) {
                        totalArrows += item.getQuantity();
                        if (arrowItemId == null) {
                            arrowItemId = itemId;
                        }
                    }
                }
            }

            // Update ammo state
            if (totalArrows > 0 && arrowItemId != null) {
                ammoVisible = true;
                ammoItemId = arrowItemId;
                ammoRemainingText = String.valueOf(totalArrows);
            } else {
                ammoVisible = false;
            }
        } catch (Exception e) {
            plugin.getPluginLogger().debug("Failed to update ammo state: " + e.getMessage());
            ammoVisible = false;
        }
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        // Append the UI file
        commandBuilder.append(UI_PATH);

        // Set initial position based on config
        String position = plugin.getPluginConfig().getHudPosition();
        Anchor anchor = new Anchor();
        anchor.setWidth(Value.of(120));

        if ("right".equalsIgnoreCase(position)) {
            anchor.setRight(Value.of(10));
        } else {
            anchor.setLeft(Value.of(10));
        }
        commandBuilder.setObject("#EquipmentPlusWrapper.Anchor", anchor);

        // Set all slot properties using current field values
        buildSlotCommands(commandBuilder);
    }

    /**
     * Returns a color based on durability percentage.
     */
    private String getDurabilityColor(int percent) {
        if (percent > 50) {
            return "#00FF00"; // Green
        } else if (percent > 25) {
            return "#FFFF00"; // Yellow
        } else {
            return "#FF0000"; // Red
        }
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    /**
     * Returns this instance as a CustomUIHud.
     * Since DurabilityHud extends CustomUIHud, we return 'this' instead of a
     * separate field.
     */
    public CustomUIHud getCustomHud() {
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public Player getPlayer() {
        return player;
    }
}
