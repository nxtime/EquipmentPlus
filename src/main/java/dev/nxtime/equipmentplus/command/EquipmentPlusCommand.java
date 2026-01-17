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

        String[] args = CommandUtils.parseArgs(ctx, "equipmentplus");

        if (args.length == 0) {
            sendHelp(ctx);
            return;
        }

        String subCommand = args[0].toLowerCase();
        EquipmentPlusConfig config = plugin.getPluginConfig();

        switch (subCommand) {
            case "reload":
                config.load();
                plugin.getHudManager().updateAllHuds(); // Apply changes
                ctx.sender().sendMessage(Message.raw("Configuration reloaded!").color("#00FF00"));
                break;

            case "togglehud":
                boolean newState = !config.isHudEnabled();
                config.setHudEnabled(newState);
                if (newState) {
                    plugin.getHudManager().updateAllHuds(); // Show/Update
                    // We re-initialize for online players in current world
                    world.getPlayers().forEach(p -> plugin.getHudManager().showHud(p));
                } else {
                    plugin.getHudManager().cleanup();
                }
                ctx.sender().sendMessage(Message.raw("HUD enabled: " + newState).color("#00FF00"));
                break;

            case "position":
                String current = config.getHudPosition();
                String newPos = current.equals("left") ? "right" : "left";
                config.setHudPosition(newPos);
                // Re-create HUDs to apply new anchor
                plugin.getHudManager().cleanup();
                world.getPlayers().forEach(p -> plugin.getHudManager().showHud(p));
                ctx.sender().sendMessage(Message.raw("HUD position set to: " + newPos).color("#00FF00"));
                break;

            default:
                sendHelp(ctx);
                break;
        }
    }

    private void sendHelp(CommandContext ctx) {
        ctx.sender().sendMessage(Message.raw("§6EquipmentPlus Commands:"));
        ctx.sender().sendMessage(Message.raw("§7/equipmentplus reload §f- Reload config"));
        ctx.sender().sendMessage(Message.raw("§7/equipmentplus togglehud §f- Toggle HUD on/off"));
        ctx.sender().sendMessage(Message.raw("§7/equipmentplus position §f- Toggle Left/Right position"));
    }
}
