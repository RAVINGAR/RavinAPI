package com.ravingarinc.api.gui.component.icon;

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
import java.util.function.BiConsumer;
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

    private final BiConsumer<@Nullable ItemStack, Player> onPlaceItem;

    public PlaceableIcon(final String identifier, final String parent, final int index, final Predicate<ItemStack> validator) {
        this(identifier, parent, index, validator, null);
    }

    public PlaceableIcon(final String identifier, final String parent, final int index, final Predicate<ItemStack> validator, final ItemStack placeholder) {
        this(identifier, parent, index, placeholder, validator, (i, p) -> {
        });
    }

    public PlaceableIcon(final String identifier, final String parent, final int index, final ItemStack placeholder, final Predicate<ItemStack> validator, final BiConsumer<@Nullable ItemStack, Player> onPlace) {
        super(identifier, parent, 3);
        this.onPlaceItem = onPlace;
        this.index = index;
        this.actions = new LinkedList<>();
        this.locked = false;
        this.validator = validator;
        if (placeholder != null) {
            final ItemMeta meta = placeholder.getItemMeta();
            meta.getPersistentDataContainer().set(getKey("identifier"), PersistentDataType.STRING, identifier);
            placeholder.setItemMeta(meta);
        }
        //Give the identifier to the placeholder
        this.placeholder = placeholder;
        this.currentItem = null;
    }

    public int getIndex() {
        return index;
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

    public boolean isValid(ItemStack item) {
        if (item == null) return false;
        return validator.test(item);
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
            final ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir() && !validator.test(cursor)) {
                return true;
            }
            handlePlacingItem(event);
            placeItem(gui, player, currentItem);
            return true;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    private void handlePlacingItem(final InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor(); //todo fix the spaghetti!
        if (isPlaceholder()) {
            this.currentItem = cursor;
            event.getWhoClicked().setItemOnCursor(null);
        } else {
            if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                if (cursor != null && cursor.isSimilar(currentItem)) {
                    assert currentItem != null; // Current Item is not null if isSimilar is true
                    cursor.setAmount(cursor.getAmount() + currentItem.getAmount());
                    event.getWhoClicked().setItemOnCursor(cursor);
                    this.currentItem = null;
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                final PlayerInventory inventory = event.getWhoClicked().getInventory();
                final int empty = inventory.firstEmpty();
                if (empty != -1 && currentItem != null) {
                    inventory.addItem(currentItem);
                    this.currentItem = null;
                }
            } else {
                if (cursor == null) {
                    event.getWhoClicked().setItemOnCursor(currentItem);
                    currentItem = null;
                } else {
                    if (cursor.isSimilar(currentItem)) {
                        assert currentItem != null; // Current Item is not null if isSimilar is true
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
    }

    public void placeItem(final BaseGui gui, final Player player, ItemStack item) {
        gui.playSound(player, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5F);
        currentItem = item;
        onPlaceItem.accept(currentItem, player);
        fillElement(gui, player);
        performAllActions(gui, player);
    }

    @Nullable
    @Override
    public ItemStack getItem() {
        return currentItem;
    }

    @Override
    public void updateItem(@Nullable final String name, @Nullable final String lore, @Nullable final Material material) {
        if (currentItem == null) {
            GuiProvider.log(Level.WARNING, "Attempted to update PlaceableIcon when it was null! This shouldn't have occurred!");
            return;
        }
        if (currentItem.isSimilar(placeholder)) {
            Interactive.super.updateItem(name, lore, material);
        }
    }

    /**
     * Sets the current item to the given argument. If null resets the current item to the placeholder. This does
     * not check if an item is valid to be put in this slot nor does it fill this element to reflect the changes.
     *
     * @param item
     */
    public void setItem(@Nullable final ItemStack item) {
        currentItem = item;
    }

    @Override
    public void addAmount(final int delta) {
        if (currentItem == null) {
            GuiProvider.log(Level.WARNING, "Attempted to update PlaceableIcon when it was null! This shouldn't have occurred!");
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

    @Override
    public void setAmount(int amount) {
        if (currentItem == null) {
            GuiProvider.log(Level.WARNING, "Attempted to update PlaceableIcon when it was null! This shouldn't have occurred!");
            return;
        }
        if (amount == 0) {
            if (!placeholder.isSimilar(currentItem)) {
                currentItem = null;
            }
        } else {
            if (amount < 65) {
                currentItem.setAmount(amount);
            }
        }
    }
}
