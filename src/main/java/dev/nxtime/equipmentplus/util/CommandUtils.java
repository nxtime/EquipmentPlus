package dev.nxtime.equipmentplus.util;

import com.hypixel.hytale.server.core.command.system.CommandContext;

import java.util.Arrays;

/**
 * Utility class for command argument parsing.
 * <p>
 * Provides standardized methods for extracting arguments from Hytale's
 * {@link CommandContext}, which only provides raw input strings.
 */
public final class CommandUtils {

    private CommandUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Parses command arguments from the context, stripping the command name.
     * <p>
     * Given input "/command status foo", returns ["status", "foo"].
     *
     * @param context     the command execution context
     * @param commandName the command name to strip (without leading slash)
     * @return array of arguments after the command name, or empty array if none
     */
    public static String[] parseArgs(CommandContext context, String commandName) {
        String input = context.getInputString();
        if (input == null) {
            return new String[0];
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length == 0) {
            return new String[0];
        }

        String first = parts[0];
        if (first.startsWith("/")) {
            first = first.substring(1);
        }

        if (first.equalsIgnoreCase(commandName)) {
            return parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        }

        return parts;
    }
}
