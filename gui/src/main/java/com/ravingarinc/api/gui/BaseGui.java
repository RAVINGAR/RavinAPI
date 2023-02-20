package com.ravingarinc.api.gui;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.api.Active;
import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.Menu;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

//If we want a generic Gui API later, move most of this to an inheriting class called TradeGui
public class BaseGui extends Element {
    protected final Inventory inventory;
    protected final JavaPlugin plugin;
    protected Player player;
    protected Menu currentMenu;
    protected boolean guiOpen;

    public BaseGui(final JavaPlugin plugin, final String name, final Inventory inventory) {
        super(name.toUpperCase(), null, -1);
        this.inventory = inventory;
        this.plugin = plugin;
        //Add possibility to have GUI of any size providing size % 2 == 1
        //Mainly support 45 or 63 size inventories
        this.player = null;
        this.guiOpen = false;
        currentMenu = null;
        children = new HashMap<>();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void shutdown() {
        getChildren().forEach(component -> {
            if (component instanceof Active active) {
                active.shutdown(this);
            }
        });
    }

    @Override
    public void fillElement(final BaseGui gui) {
        this.currentMenu.fillElement(this);
    }

    @Override
    public Class<BaseGui> getThisClass() {
        return BaseGui.class;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isGuiOpen() {
        return guiOpen;
    }

    public Menu getCurrentMenu() {
        return this.currentMenu;
    }

    public void updateCurrentMenu(final String pointer) {
        final Optional<Menu> nextMenu = findComponent(Component.MENU, pointer);
        if (nextMenu.isPresent()) {
            this.currentMenu = nextMenu.get();
            refresh();
        } else {
            I.log(Level.WARNING, "Could not update menu to " + pointer + " as it doesn't exist!");
        }
    }

    public void refresh() {
        fillElement(this);
    }

    public void handleClickedItem(final InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        Optional<Interactive> interactive = Optional.empty();
        if (GuiProvider.hasMeta(item, "identifier")) {
            interactive = currentMenu.findComponent(Component.INTERACTIVE, GuiProvider.getMetaString(item, "identifier"));
        } else {
            //If clicked item does not have identifier, (Either item is null, or it's a placeable item)
            //todo maybe figure out how shift clicks from the inventory can auto place in placeable icons?
            final List<PlaceableIcon> placeables = currentMenu.findAllComponents(Component.PLACEABLE_ICON);
            for (final PlaceableIcon placed : placeables) {
                if (placed.getInventoryLocation() == event.getSlot()) {
                    interactive = Optional.of(placed);
                    break;
                }
            }
            if (interactive.isEmpty() && event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                for (final PlaceableIcon placed : placeables) {
                    if (!placed.isPlaceholder() && placed.handleClickedItem(this, event)) {
                        interactive = Optional.of(placed);
                        break;
                    }
                }
            }
        }
        interactive.ifPresentOrElse(i -> i.handleClickedItem(this, event), this::denySound);
    }

    public void playSound(final Sound sound, final float pitch) {
        final Player clicker = player.getPlayer();
        clicker.playSound(clicker.getLocation(), sound, 0.7F, pitch);
    }

    public void denySound() {
        playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 0.1F);
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    /*
    Returns ANY menu contained within this GUI
     */

    public Player getPlayer() {
        if (player == null) {
            I.log(Level.SEVERE, "Attempted to access player when player has not been initialised! Incorrect initialisation of BaseGui!");
        }
        return player;
    }

    public void setPlayer(final Player player) {
        if (this.player == null) {
            this.player = player;
        }
    }

    public void openGui() {
        //At this point, when the GUI is created it should have a main menu fill
        //After that whenever this is called it should resume on whatever gui the user was previously at
        if (!guiOpen) {
            player.openInventory(inventory);
            if (currentMenu == null) {
                currentMenu = findComponent(Component.MENU, "MAIN").orElseThrow();
            }
            currentMenu.fillElement(this); //Refresh gui
            guiOpen = true;
        }
    }

    /**
     * Set's this GUIs state to closed. Does NOT close inventory.
     */
    public void closeGui() {
        if (guiOpen) {
            guiOpen = false;
            findFirstComponent(Component.QUEUEABLE).ifPresent(q -> q.fillElement(this));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BaseGui baseGui = (BaseGui) o;
        return identifier.equals(baseGui.identifier) && player.equals(baseGui.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, player);
    }
}
