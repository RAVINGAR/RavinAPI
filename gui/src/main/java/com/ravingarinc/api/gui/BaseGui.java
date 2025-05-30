package com.ravingarinc.api.gui;

import com.ravingarinc.api.gui.api.Active;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.action.Action;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * BaseGui represents an InventoryHolder that exists at some given place and can be viewed by multiple players.
 * To open this inventory, simply call {@link Player#openInventory(Inventory)} with the parameter of
 * {@link BaseGui#getInventory()}.
 * A BaseGui is not destroyed until {@link #destroy()} is called.
 * It is up to the discretion of the developer to determine whether a BaseGui would be suitable for multiple players to use
 * at one time. Since a single BaseGui represents a single inventory, one player clicking on a GUI would update it for all
 * viewers. General rule - If a BaseGui is shared, then icons and actions SHOULD NOT be different per player.
 */
public class BaseGui extends Element implements InventoryHolder {
    private final UUID internalId;
    protected final Inventory inventory;
    protected final Plugin plugin;

    @Deprecated
    protected Player player;

    protected List<Player> players;
    protected Menu currentMenu;

    private boolean requiresRefresh = false;

    private final Map<String, Object> memoryObjects;

    @Deprecated
    public BaseGui(final JavaPlugin plugin, final String name, final Inventory inventory) {
        this(plugin, name, inventory.getSize());
    }

    public BaseGui(final Plugin plugin, final String name, final Integer inventorySize) {
        super(name.toUpperCase(), null, -1);
        GuiProvider.register(plugin);
        this.internalId = UUID.randomUUID();
        this.inventory = plugin.getServer().createInventory(this, inventorySize, formatString(name));
        this.players = new LinkedList<>();
        this.plugin = plugin;
        currentMenu = null;
        children = new HashMap<>();
        this.memoryObjects = new HashMap<>();
    }

    public void setMemoryObject(String key, Object object) {
        memoryObjects.put(key, object);
    }

    @Nullable
    public Object getMemoryObject(String key) {
        return memoryObjects.get(key);
    }

    @Nullable
    public Object removeMemoryObject(String key) {
        return memoryObjects.remove(key);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void destroy() {
        new ArrayList<>(players).forEach(player -> {
            findFirstComponent(Component.QUEUEABLE).ifPresent(q -> q.fillElement(this, player));
            player.closeInventory();
            players.remove(player);
        });
        getChildren().forEach(component -> {
            if (component instanceof Active active) {
                active.shutdown(this);
            }
        });
        GuiProvider.register(plugin).unregister(this);
    }

    public void queueRefresh() {
        requiresRefresh = true;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        fillElement(player);
    }

    public void fillElement(final Player player) {
        if (players.contains(player)) {
            this.currentMenu.fillElement(this, player);
        }
    }

    @Override
    public Class<BaseGui> getThisClass() {
        return BaseGui.class;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Menu getCurrentMenu() {
        return this.currentMenu;
    }

    public void updateCurrentMenu(final String pointer, final Player player) {
        final Optional<Menu> nextMenu = findComponent(Component.MENU, pointer);
        if (nextMenu.isPresent()) {
            this.currentMenu = nextMenu.get();
            fillElement(this, player);
        } else {
            GuiProvider.log(Level.WARNING, "Could not update menu to " + pointer + " as it doesn't exist!");
        }
    }

    /**
     * Play an action for all current viewers of this gui
     *
     * @param action The action.
     */
    public void playAction(Action action) {
        new ArrayList<>(players).forEach(player -> {
            if (this.equals(player.getOpenInventory().getTopInventory().getHolder())) {
                action.performAction(this, player);
            }
        });
    }

    /**
     * Handle the case where the player clicks their own inventory whilst having a gui open.
     * <p>
     * Returns whether to cancel the corresponding event or not.
     */
    public boolean handlePlayerInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            final ItemStack cursor = event.getCursor();
            if (cursor == null) return false;
            final var icons = currentMenu.getChildren()
                    .stream()
                    .filter(c -> c instanceof PlaceableIcon)
                    .map(c -> (PlaceableIcon) c)
                    .collect(Collectors.toSet());
            for (PlaceableIcon icon : icons) {
                if (icon.isPlaceholder()) continue;
                final ItemStack placedItem = icon.getItem();
                if (cursor.isSimilar(placedItem)) {
                    if (icon.isLocked()) return true;
                    icon.placeItem(this, player, null);
                    cursor.setAmount(cursor.getAmount() + placedItem.getAmount());
                    event.getView().setCursor(cursor);
                    return true;
                }
            }
            return false;
        }

        if (event.isShiftClick()) {
            final ItemStack item = event.getCurrentItem();
            if (item == null) return false;
            final var icons = currentMenu.getChildren()
                    .stream()
                    .filter(c -> c instanceof PlaceableIcon)
                    .map(c -> (PlaceableIcon) c)
                    .sorted(Comparator.comparingInt(PlaceableIcon::getIndex))
                    .toList();
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                for (PlaceableIcon icon : icons) {
                    final ItemStack placedItem = icon.getItem();
                    if (placedItem == null) continue;
                    if (placedItem.isSimilar(item)) {
                        if (icon.isLocked() || icon.isPlaceholder()) return true;
                        placedItem.setAmount(item.getAmount() + placedItem.getAmount());
                        icon.placeItem(this, player, placedItem);
                        event.setCurrentItem(null);
                        return true;
                    }
                }
            }
            // This will only execute if the item was not placed anywhere
            for (PlaceableIcon icon : icons) {
                if (icon.isLocked()) continue;
                if (icon.isPlaceholder() && icon.isValid(item)) {
                    icon.placeItem(this, player, item);
                    event.setCurrentItem(null);
                    break;
                }
            }
            return true; // This must return here in the case that placeable icons are locked.
        }
        return false;
    }

    public void handleClickedItem(final InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();
        final String meta = getMetaString(item, "identifier");
        Optional<Interactive> interactive = Optional.empty();
        if (meta == null) {
            //If clicked item does not have identifier, (Either item is null, or it's a placeable item)
            final List<PlaceableIcon> placeables = currentMenu.findAllComponents(Component.PLACEABLE_ICON);
            for (final PlaceableIcon placed : placeables) {
                if (placed.getInventoryLocation() == event.getSlot()) {
                    interactive = Optional.of(placed);
                    break;
                }
            }
            if (interactive.isEmpty() && event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                for (final PlaceableIcon placed : placeables) {
                    if (!placed.isPlaceholder() && placed.handleClickedItem(this, event, player)) {
                        interactive = Optional.of(placed);
                        break;
                    }
                }
            }
        } else {
            interactive = currentMenu.findComponent(Component.INTERACTIVE, meta);
        }
        interactive.ifPresentOrElse(i -> {
            i.handleClickedItem(this, event, player);
            // Todo, make it so if any gui actions happen that require a gui update they should do it automatically
            if (requiresRefresh) {
                fillElement(player);
            }
        }, () -> denySound(player));
    }

    public String getMetaString(final ItemStack item, final String key) {
        if (item == null) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(getKey(key), PersistentDataType.STRING);
    }

    public void playSound(final Player player, final Sound sound, final float pitch) {
        player.playSound(player.getLocation(), sound, 0.7F, pitch);
    }

    public void denySound(Player player) {
        playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 0.1F);
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    /**
     * Returns the first player currently viewing the BaseGui.
     *
     * @return
     * @deprecated In most cases, the GuiInstance class will be used instead as this holds the current clicker of said action.
     */
    @Deprecated
    public Player getPlayer() {
        return players.get(0);
    }

    /**
     * Handle opening this gui. Does not open the physical inventory. This generally is called by a Listener and nothing else
     */
    public void handleOpen(Player player) {
        if (!players.contains(player)) {
            if (currentMenu == null) {
                currentMenu = findComponent(Component.MENU, "MAIN").orElseThrow();
            }
            players.add(player);
            currentMenu.fillElement(this, player); //Refresh gui
        }
    }

    /**
     * Handle closing this gui. Does not close the physical inventory. This generally is called by a Listener and nothing else
     */
    public void handleClose(Player player) {
        if (players.contains(player)) {
            findFirstComponent(Component.QUEUEABLE).ifPresent(q -> q.fillElement(this, player));
            players.remove(player);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseGui baseGui = (BaseGui) o;
        return Objects.equals(internalId, baseGui.internalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId);
    }
}
