package com.ravingarinc.api.module;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RavinPlugin extends Plugin {
    void loadModules();

    void loadCommands();

    void reload();

    <T extends Module> void addModule(final Class<T> module);

    /**
     * Get the manager of the specified type otherwise an IllegalArgumentException is thrown.
     *
     * @param type The manager type
     * @param <T>  The type
     * @return The manager
     */
    <T extends Module> T getModule(final Class<T> type);

    @Nullable
    PluginCommand getCommand(@NotNull String name);


}
