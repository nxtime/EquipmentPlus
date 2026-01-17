package dev.nxtime.equipmentplus.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class UiUtils {

    private UiUtils() {
        // Utility class
    }

    public static String loadUiFile(Class<?> clazz, String path) {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                return "Group #Error { Label #ErrorLabel { Text: \"UI file not found: " + path + "\"; } }";
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Group #Error { Label #ErrorLabel { Text: \"Error loading UI: " + e.getMessage() + "\"; } }";
        }
    }
}
