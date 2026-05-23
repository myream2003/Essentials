package de.myream.essentialsfolia.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Colors {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private static final java.util.regex.Pattern STRIP_PATTERN =
            java.util.regex.Pattern.compile("[&§][0-9a-fk-orA-FK-OR]");

    private Colors() {}

    public static Component parse(String text) {
        return LEGACY.deserialize(text);
    }

    public static String colorize(String text) {
        return text.replace('&', '§');
    }

    public static String stripColor(String text) {
        return STRIP_PATTERN.matcher(text).replaceAll("");
    }
}
