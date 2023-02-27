package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class GuiProvider {
    private static GuiProvider instance;
    private final Map<UUID, List<BaseGui>> guiPlayers;

    private final JavaPlugin plugin;

    private GuiProvider(final JavaPlugin plugin) {
        this.guiPlayers = new HashMap<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new GuiListener(), plugin);
    }

    public static GuiProvider getInstance(final JavaPlugin plugin) {
        if (instance == null) {
            instance = new GuiProvider(plugin);
        }
        return instance;
    }

    public static boolean hasMeta(final @Nullable ItemStack item, final String key) {
        if (item == null) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(getKey(key), PersistentDataType.STRING);
    }

    public static boolean getMetaByte(final @Nullable ItemStack item, final String key) {
        if (item == null) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        final Byte b = meta.getPersistentDataContainer().get(getKey(key), PersistentDataType.BYTE);
        if (b == null) {
            return false;
        }
        return b == 1;
    }

    public static String getMetaString(final ItemStack item, final String key) {
        if (item == null) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(getKey(key), PersistentDataType.STRING);
    }

    public static NamespacedKey getKey(final String key) {
        return new NamespacedKey(instance.plugin, key);
    }

    public boolean handleClickEvent(final InventoryClickEvent event) {
        final Optional<BaseGui> optional = getOpenGui((Player) event.getWhoClicked());
        if (optional.isPresent()) {
            final BaseGui gui = optional.get();
            final int slot = event.getRawSlot();
            if (slot >= 0 && slot < gui.getInventory().getSize()) {
                gui.handleClickedItem(event);
                return true;
            }
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                gui.handleClickedItem(event);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private Optional<BaseGui> getOpenGui(final Player player) {
        final List<BaseGui> guis = guiPlayers.get(player.getUniqueId());
        if (guis != null) {
            return guis.stream().filter(BaseGui::isGuiOpen).findFirst();
        }
        return Optional.empty();
    }

    private Optional<BaseGui> getCustomGui(final String identifier, final Player player) {
        final List<BaseGui> guis = guiPlayers.get(player.getUniqueId());
        if (guis == null) {
            return Optional.empty();
        } else {
            return guis.stream().filter(base -> base.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
        }
    }

    @Deprecated
    public void openCustomGui(final GuiBuilder<?> builder, final Player player) {
        openCustomGui("", () -> builder, player);
    }

    /**
     * Attempts to open a custom gui using the given builder
     *
     * @param builder The builder
     * @param player  The player
     */
    public void openCustomGui(final String identifier, final Supplier<GuiBuilder<?>> builder, final Player player) {
        final List<BaseGui> guis = guiPlayers.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>());
        guis.stream()
                .filter(gui -> gui.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst()
                .ifPresentOrElse(BaseGui::openGui, () -> {
                    final BaseGui gui = builder.get().build(player);
                    guis.add(gui);
                    gui.openGui();
                });
    }

    /**
     * Resets a custom gui, aka removes it and recreates it using the given builder.
     */
    public void resetCustomGui(final GuiBuilder<?> builder, final Player player) {
        final List<BaseGui> guis = guiPlayers.remove(player.getUniqueId());
        if (guis != null) {
            guis.stream()
                    .filter(g -> g.getIdentifier().equalsIgnoreCase(builder.identifier()))
                    .findFirst()
                    .ifPresent(g -> {
                        g.closeGui();
                        guis.remove(g);
                    });
        }
        openCustomGui(builder.identifier(), () -> builder, player);
    }

    /**
     * Closes the custom gui with the given identifier. If no gui is found, then nothing happens.
     *
     * @param identifier
     * @param player
     */
    public void closeCustomGui(final String identifier, final Player player) {
        getCustomGui(identifier, player).ifPresent(BaseGui::closeGui);
    }

    public void shutdownAllGuis(final Player player) {
        final List<BaseGui> guis = guiPlayers.remove(player.getUniqueId());
        if (guis != null) {
            guis.forEach(BaseGui::shutdown);
        }
    }

    public List<BaseGui> getAllGuis(final Player player) {
        final List<BaseGui> guis = guiPlayers.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>());
        return Collections.unmodifiableList(guis);
    }

    public void shutdown() {
        guiPlayers.values().forEach(guiList -> {
            guiList.forEach(BaseGui::shutdown);
            guiList.clear();
        });
        guiPlayers.clear();
    }

    protected class GuiListener implements Listener {
        @EventHandler
        public void onGuiClick(final InventoryClickEvent event) {
            // todo make it so you cant just spam click, basically queue a scheduled event, and only accept new click events
            // until the last click event finishes
            if (handleClickEvent(event)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            shutdownAllGuis(event.getPlayer());
        }

        @EventHandler
        public void onInventoryClose(final InventoryCloseEvent event) {
            closeCustomGui(event.getView().getTitle(), (Player) event.getPlayer());
        }
    }
}
