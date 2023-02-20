package com.ravingarinc.api.module;

import com.ravingarinc.api.I;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RavinPlugin extends JavaPlugin {

    public boolean debug;
    protected Logger logger;

    protected Map<Class<? extends Module>, Module> modules;

    public void log(final Level level, final String message, @Nullable final Throwable throwable, final Object... replacements) {
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

    public void log(final Level level, final String message, final Throwable throwable) {
        logger.log(level, message, throwable);
    }

    public void logIfDebug(final Supplier<String> message, final Object... replacements) {
        if (debug) {
            log(Level.WARNING, message.get(), null, replacements);
        }
    }

    public void runIfDebug(final Runnable runnable) {
        if (debug) {
            runnable.run();
        }
    }

    @Override
    public void onLoad() {
        this.logger = this.getLogger();
        this.modules = new LinkedHashMap<>();
        I.load(this);
    }

    @Override
    public void onEnable() {
        loadModules();
        load();
        loadCommands();
    }

    public abstract void loadModules();

    public abstract void loadCommands();

    public void load() {
        modules.values().forEach(manager -> {
            try {
                manager.initialise();
            } catch (final ModuleLoadException e) {
                I.log(Level.SEVERE, e.getMessage());
            }
        });

        int loaded = 0;
        for (final Module module : modules.values()) {
            if (module.isLoaded()) {
                loaded++;
            }
        }
        if (loaded > 1) {
            if (loaded == modules.size()) {
                I.log(Level.INFO, "%s has been enabled successfully!", getName());
            } else {
                I.log(Level.INFO, "%s has been partially enabled successfully!", getName());
                I.log(Level.WARNING, "%s module/s have failed to load!", (modules.size() - loaded));
            }
        } else {
            I.log(Level.INFO, "No modules could be loaded! %s will now shutdown...", getName());
            this.onDisable();
        }

    }

    public void reload() {
        cancel();
        load();
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void cancel() {
        final List<Module> reverseOrder = new ArrayList<>(modules.values());
        Collections.reverse(reverseOrder);
        reverseOrder.forEach(module -> {
            if (module.isLoaded()) {
                try {
                    module.cancel();
                    module.setLoaded(false);
                } catch (final Exception e) {
                    log(Level.SEVERE, "Encountered issue shutting down module '%s'!", e, module.getName());
                }
            }
        });
    }

    protected <T extends Module> void addModule(final Class<T> module) {
        final Optional<? extends Module> opt = Module.initialise(this, module);
        opt.ifPresent(t -> modules.put(module, t));
    }

    /**
     * Get the manager of the specified type otherwise an IllegalArgumentException is thrown.
     *
     * @param type The manager type
     * @param <T>  The type
     * @return The manager
     */
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(final Class<T> type) {
        final Module module = modules.get(type);
        if (module == null) {
            throw new IllegalArgumentException("Could not find module of type " + type.getName() + ". Contact developer! Most likely #.getManager() has been called from a Module's constructor or module was not added!");
        }
        return (T) module;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void onDisable() {
        cancel();
        this.getServer().getScheduler().cancelTasks(this);
        I.log(Level.INFO, getName() + " is disabled.");
    }
}
