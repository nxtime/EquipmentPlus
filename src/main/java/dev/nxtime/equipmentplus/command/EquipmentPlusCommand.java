package dev.nxtime.equipmentplus.command;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.worldgen.loader.util.ColorUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.Message;
import dev.nxtime.equipmentplus.EquipmentPlusPlugin;
import dev.nxtime.equipmentplus.EquipmentPlusConfig;
import dev.nxtime.equipmentplus.util.CommandUtils;
import dev.nxtime.equipmentplus.util.ColorConfig;
import dev.nxtime.equipmentplus.gui.EquipmentPlusGuiPage;
import com.hypixel.hytale.server.core.entity.entities.Player;

import javax.annotation.Nonnull;

/**
 * Command to manage EquipmentPlus settings.
 * /equipmentplus [reload|togglehud|position]
 */
public class EquipmentPlusCommand extends AbstractPlayerCommand {

    private final EquipmentPlusPlugin plugin;

    public EquipmentPlusCommand(EquipmentPlusPlugin plugin) {
        super("equipmentplus", "Manage EquipmentPlus settings");
        this.plugin = plugin;

        setAllowsExtraArguments(true);
        requirePermission("dev.nxtime.equipmentplus.command.admin");
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null)
            return;

        String[] args = CommandUtils.parseArgs(ctx, "equipmentplus");

        if (args.length == 0 || "gui".equalsIgnoreCase(args[0])) {
            openGui(player);
            return;
        }

        String subCommand = args[0].toLowerCase();
        EquipmentPlusConfig config = plugin.getPluginConfig();

        switch (subCommand) {
            case "reload":
                config.load();
                plugin.getHudManager().cleanup();
                world.getPlayers().forEach(p -> plugin.getHudManager().showHud(p));
                player.sendMessage(Message.join(
                        Message.raw(ColorConfig.BRAND).color(ColorConfig.PREFIX_COLOR),
                        Message.raw("Configuration reloaded!").color(ColorConfig.SUCCESS)));
                break;

            case "togglehud":
                boolean newState = !config.isHudEnabled();
                config.setHudEnabled(newState);
                if (newState) {
                    world.getPlayers().forEach(p -> plugin.getHudManager().showHud(p));
                } else {
                    plugin.getHudManager().cleanup();
                }
                player.sendMessage(Message.join(
                        Message.raw(ColorConfig.BRAND).color(ColorConfig.PREFIX_COLOR),
                        Message.raw("HUD enabled: ").color(ColorConfig.TEXT),
                        Message.raw(String.valueOf(newState))
                                .color(newState ? ColorConfig.SUCCESS : ColorConfig.ERROR)));
                break;

            case "position":
                String current = config.getHudPosition();
                String newPos = current.equalsIgnoreCase("left") ? "right" : "left";
                config.setHudPosition(newPos);
                plugin.getHudManager().cleanup();
                world.getPlayers().forEach(p -> plugin.getHudManager().showHud(p));
                player.sendMessage(Message.join(
                        Message.raw(ColorConfig.BRAND).color(ColorConfig.PREFIX_COLOR),
                        Message.raw("HUD position set to: ").color(ColorConfig.TEXT),
                        Message.raw(newPos).color(ColorConfig.HIGHLIGHT)));
                break;

            default:
                sendHelp(player);
                break;
        }
    }

    private void openGui(Player player) {
        var ref = player.getReference();
        if (ref != null && ref.isValid()) {
            var store = ref.getStore();
            var world = store.getExternalData().getWorld();

            world.execute(() -> {
                var playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                if (playerRefComponent != null) {
                    player.getPageManager().openCustomPage(ref, store,
                            new EquipmentPlusGuiPage(playerRefComponent,
                                    com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime.CanDismiss));
                }
            });
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(Message.join(
                Message.raw(ColorConfig.BRAND).color(ColorConfig.PREFIX_COLOR),
                Message.raw("Available Commands:").color(ColorConfig.HIGHLIGHT)));
        player.sendMessage(Message.join(
                Message.raw("  /equipmentplus ").color(ColorConfig.TEXT),
                Message.raw("- Open settings GUI").color(ColorConfig.HIGHLIGHT)));
        player.sendMessage(Message.join(
                Message.raw("  /equipmentplus reload ").color(ColorConfig.TEXT),
                Message.raw("- Reload configuration").color(ColorConfig.HIGHLIGHT)));
        player.sendMessage(Message.join(
                Message.raw("  /equipmentplus togglehud ").color(ColorConfig.TEXT),
                Message.raw("- Toggle HUD visibility").color(ColorConfig.HIGHLIGHT)));
        player.sendMessage(Message.join(
                Message.raw("  /equipmentplus position ").color(ColorConfig.TEXT),
                Message.raw("- Switch Left/Right position").color(ColorConfig.HIGHLIGHT)));
    }
}
