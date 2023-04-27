package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GuiProvider listens for important events and manages guis. It is registered automatically, otherwise when a gui
 * is created, it is automatically registered!
 * <p>
 * It should be
 */
public class GuiProvider implements Listener {
    private static GuiProvider instance;
    private final Map<UUID, Long> lastClicks;

    private final Set<BaseGui> registered;
    private final Logger logger;


    private GuiProvider(JavaPlugin plugin) {
        this.lastClicks = new ConcurrentHashMap<>();
        this.registered = ConcurrentHashMap.newKeySet();
        this.logger = plugin.getLogger();
    }

    /**
     * Tries to register this GuiProvider if it has not already been registered
     */
    public static GuiProvider register(final JavaPlugin plugin) {
        if (instance == null) {
            instance = new GuiProvider(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
        return instance;
    }

    /**
     * Shutdown and unregister this provider. This is optional in some cases, but recommended when shutting down the server
     */
    public static void unregister() {
        if (instance == null) {
            return;
        }
        new ArrayList<>(instance.registered).forEach(BaseGui::destroy);
    }

    public void unregister(BaseGui gui) {
        this.registered.remove(gui);
    }

    public static void log(final Level level, final String message, final Object... replacements) {
        log(level, message, null, replacements);
    }

    public static void log(final Level level, final String message, @Nullable final Throwable throwable, final Object... replacements) {
        if (instance == null) {
            return;
        }
        String format = message;
        for (final Object replacement : replacements) {
            format = format.replaceFirst("%s", replacement.toString());
        }
        if (throwable == null) {
            instance.logger.log(level, format);
        } else {
            instance.logger.log(level, format, throwable);
        }
    }

    public static void log(final Level level, final String message, final Throwable throwable) {
        if (instance == null) {
            return;
        }
        instance.logger.log(level, message, throwable);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui gui) {
            gui.handleOpen((Player) event.getPlayer());
            registered.add(gui);
        }
    }

    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null || !(inventory.getHolder() instanceof BaseGui gui)) {
            return;
        }
        event.setCancelled(true);
        UUID uuid = event.getWhoClicked().getUniqueId();
        long current = System.currentTimeMillis();
        long lastClick = lastClicks.computeIfAbsent(uuid, (u) -> current);
        if (current > lastClick + 100) {
            lastClicks.put(uuid, current);
            gui.handleClickedItem(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui gui) {
            gui.handleClose((Player) event.getPlayer());
        }
    }
}
