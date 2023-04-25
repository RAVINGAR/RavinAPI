package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.api.Interactive;
import com.ravingarinc.api.gui.builder.GuiProvider;
import com.ravingarinc.api.gui.component.action.Action;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

//Placeholders will only allow name and lore changes via Dynamic, if the current item is the placeholder.
public class PlaceableIcon extends Element implements Interactive {
    private final List<Action> actions;
    private final int index;
    private final ItemStack placeholder;
    private final Predicate<ItemStack> validator;
    private boolean locked;
    private @Nullable ItemStack currentItem;

    public PlaceableIcon(final String identifier, final String parent, final int index, final Predicate<ItemStack> validator) {
        this(identifier, parent, index, validator, null);
    }

    @SuppressWarnings("PMD.UnusedAssignment")
    public PlaceableIcon(final String identifier, final String parent, final int index, final Predicate<ItemStack> validator, final ItemStack placeholder) {
        super(identifier, parent, 3);
        this.index = index;
        this.actions = new LinkedList<>();
        this.locked = false;
        this.validator = validator;
        if (placeholder != null) {
            final ItemMeta meta = placeholder.getItemMeta();
            meta.getPersistentDataContainer().set(GuiProvider.getKey("identifier"), PersistentDataType.STRING, identifier);
            placeholder.setItemMeta(meta);
        }
        //Give the identifier to the placeholder
        this.placeholder = placeholder;
        this.currentItem = null;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public int getInventoryLocation() {
        return index;
    }

    /**
     * Must be called from a context that isn't an infinite loop. AKA Generally, speaking should not be called
     * from an observer of a Placeable Icon in which is being reset.
     */
    public void reset() {
        this.currentItem = null;
    }

    public boolean isPlaceholder() {
        return currentItem == null || hasMeta(PersistentDataType.STRING, "identifier");
    }

    @Override
    public void addAction(final Action action) {
        if (action != null) {
            actions.add(action);
        }
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        super.fillElement(gui, player);
        if (currentItem == null || currentItem.getType().isAir()) {
            currentItem = placeholder.clone();
        }
        gui.getInventory().setItem(index, currentItem);
    }

    @Override
    public Class<PlaceableIcon> getThisClass() {
        return PlaceableIcon.class;
    }

    @Override
    public void performAllActions(final BaseGui gui, Player player) {
        actions.forEach(action -> action.performAction(gui, player));
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    public boolean handleClickedItem(final BaseGui gui, final InventoryClickEvent event, Player player) {
        if (locked) {
            gui.denySound(player);
            return false;
        } else {
            boolean handled = true;
            final boolean isPlaceholder = isPlaceholder();
            final ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                if (validator.test(cursor)) {
                    handled = handlePlacingItem(event, isPlaceholder);
                    performAllActions(gui, player);
                }
            } else if (!isPlaceholder) { //If is not placeholder and has empty hand
                handled = handlePlacingItem(event, false);
                performAllActions(gui, player);
            }
            if (handled) {
                gui.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5F);
            }
            return handled;
        }
    }

    private boolean handlePlacingItem(final InventoryClickEvent event, final boolean isPlaceholder) {
        final ItemStack cursor = event.getCursor(); //todo fix the spaghetti!
        if (isPlaceholder) {
            currentItem = cursor;
            event.getWhoClicked().setItemOnCursor(null);
        } else {
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                if (cursor != null && cursor.isSimilar(currentItem)) {
                    cursor.setAmount(cursor.getAmount() + currentItem.getAmount());
                    event.getWhoClicked().setItemOnCursor(cursor);
                    currentItem = null;
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                final PlayerInventory inventory = event.getWhoClicked().getInventory();
                final int empty = inventory.firstEmpty();
                if (empty != -1) {
                    inventory.addItem(currentItem);
                    currentItem = null;
                }
            } else {
                if (cursor == null) {
                    event.getWhoClicked().setItemOnCursor(currentItem);
                    currentItem = null;
                } else {
                    if (cursor.isSimilar(currentItem)) {
                        currentItem.setAmount(currentItem.getAmount() + cursor.getAmount());
                        event.getWhoClicked().setItemOnCursor(null);
                    } else { //Swapping items
                        event.getWhoClicked().setItemOnCursor(currentItem);
                        currentItem = cursor;
                    }
                }
            }
        }
        event.setCurrentItem(null);
        return true;
    }

    @Nullable
    @Override
    public ItemStack getItem() {
        return currentItem;
    }

    @Override
    public void updateItem(@Nullable final String name, @Nullable final String lore, @Nullable final Material material) {
        if (currentItem == null) {
            I.log(Level.WARNING, "Attempted to update PlaceableIcon when it was null! This shouldn't have occurred!");
            return;
        }
        if (currentItem.isSimilar(placeholder)) {
            Interactive.super.updateItem(name, lore, material);
        }
    }

    @Override
    public void addAmount(final int delta) {
        if (currentItem == null) {
            I.log(Level.WARNING, "Attempted to update PlaceableIcon when it was null! This shouldn't have occurred!");
            return;
        }
        final int amount = currentItem.getAmount() + delta;
        if (amount > 0) {
            if (amount < 65) {
                currentItem.setAmount(amount);
            }
        } else if (!placeholder.isSimilar(currentItem)) {
            currentItem = null;
        }
    }
}
