package com.ravingarinc.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static logger class used for shortcut logging.
 */
public class I {
    private static Logger logger;

    private static boolean debug = false;

    public I() {
    }

    public static void load(final JavaPlugin plugin, final boolean debug) {
        I.logger = plugin.getLogger();
        I.debug = debug;
    }

    public static void log(final Level level, final String message, final Object... replacements) {
        log(level, message, null, replacements);
    }

    public static void log(final Level level, final String message, @Nullable final Throwable throwable, final Object... replacements) {
        String format = message;
        for (final Object replacement : replacements) {
            format = format.replaceFirst("%s", replacement.toString());
        }
        if (throwable == null) {
            logger.log(level, format);
        } else {
            logger.log(level, format, throwable);
        }
    }

    public static void log(final Level level, final String message, final Throwable throwable) {
        logger.log(level, message, throwable);
    }

    public static void logIfDebug(final Supplier<String> message, final Object... replacements) {
        if (debug) {
            log(Level.WARNING, message.get(), null, replacements);
        }
    }
}
