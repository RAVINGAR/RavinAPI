package com.ravingarinc.api.gui.builder;

import com.ravingarinc.api.gui.api.Component;
import com.ravingarinc.api.gui.component.icon.PlaceableIcon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlaceableIconPreBuilder {

    private final ItemStack placeholder;
    private final int index;
    private boolean removeOnPickup = false;
    private Predicate<ItemStack> itemValidator = (i) -> true;
    private BiConsumer<ItemStack, Player> onPlaceItem = (i, p) -> {
    };

    public PlaceableIconPreBuilder(final String placeholderName, final String placeholderLore,
                                   final Material placeholderMaterial, final int index) {
        this.placeholder = new ItemStack(placeholderMaterial);
        placeholder.editMeta((meta) -> {
            meta.displayName(Component.format(placeholderName));
            meta.lore(Component.formatList(placeholderLore));
        });
        this.index = index;
    }

    public PlaceableIconPreBuilder editItem(Consumer<ItemStack> builder) {
        builder.accept(placeholder);
        return this;
    }

    /**
     * If an item has been placed in the given slot, will picking it up remove it from the player's cursor?
     * (Todo This functionality needs investigation)
     */
    public PlaceableIconPreBuilder removeOnPickup(boolean removeOnPickup) {
        this.removeOnPickup = removeOnPickup;
        return this;
    }

    /**
     * Set the validator for when an item is attempted to be placed in this placeable icon. If the predicate
     * returns true then the item can be placed, if false, then it cannot.
     */
    public PlaceableIconPreBuilder itemValidator(Predicate<ItemStack> itemValidator) {
        if (itemValidator == null) {
            throw new IllegalArgumentException("itemValidator cannot be null");
        }
        this.itemValidator = itemValidator;
        return this;
    }

    /**
     * This consumer is called after an item is successfully changed on the placeable icon. If an item is removed
     * the ItemStack in question will be null, so handling must account for that. Otherwise the item stack
     * passed to this function will be the updated stack contained within the placeable icon.
     */
    public PlaceableIconPreBuilder onChangeItem(BiConsumer<ItemStack, Player> onChangeItem) {
        this.onPlaceItem = onChangeItem;
        return this;
    }


    protected PlaceableIcon build(final String identifier, final String lastMenu) {
        return new PlaceableIcon(identifier, lastMenu, index, placeholder, removeOnPickup, itemValidator, onPlaceItem);
    }
}