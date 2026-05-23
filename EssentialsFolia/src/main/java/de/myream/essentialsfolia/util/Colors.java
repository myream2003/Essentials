package de.myream.essentialsfolia.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Colors {

    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    private Colors() {}

    public static Component parse(String text) {
        return SERIALIZER.deserialize(text);
    }

    public static String strip(String text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(parse(text))
                .replaceAll("§[0-9a-fk-or]", "");
    }

    public static String colorize(String text) {
        return text.replace('&', '§');
    }

    public static String stripColor(String text) {
        return text.replaceAll("&[0-9a-fk-orA-FK-OR]", "")
                   .replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }
}
