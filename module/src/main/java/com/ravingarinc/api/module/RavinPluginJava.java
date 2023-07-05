package com.ravingarinc.api.module;

import com.ravingarinc.api.I;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public abstract class RavinPluginJava extends JavaPlugin implements RavinPlugin {

    protected Map<Class<? extends Module>, Module> modules;

    @Override
    public void onLoad() {
        this.modules = new LinkedHashMap<>();
        I.load(this, false);
    }

    @Override
    public void onEnable() {
        loadModules();
        loadCommands();
        load();
    }

    public void load() {
        modules.values().forEach(module -> {
            try {
                module.initialise();
            } catch (final ModuleLoadException e) {
                I.log(module.isRequired ? Level.SEVERE : Level.INFO, e.getMessage(), e.getCause());
            }
        });

        int loaded = 0;
        for (final Module module : modules.values()) {
            if (module.isLoaded() || !module.isRequired) {
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

    @Override
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
                    I.log(Level.SEVERE, "Encountered issue shutting down module '%s'!", e, module.getName());
                }
            }
        });
    }

    @Override
    public <T extends Module> void addModule(final Class<T> module) {
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
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(final Class<T> type) {
        final Module module = modules.get(type);
        if (module == null) {
            throw new IllegalArgumentException("Could not find module of type " + type.getName() + ". Contact developer! Most likely #.getModule() has been called from a Module's constructor or module was not added!");
        }
        return (T) module;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void onDisable() {
        cancel();
        this.getServer().getScheduler().cancelTasks(this);
        I.log(Level.INFO, getName() + " is now disabled");
    }
}
