package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Element;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class Page extends Element {
    private final int[] slots;
    private final List<ItemStack> iconsToPlace;
    private int currentPage;

    public Page(final String identifier, final String parent, final int... slots) {
        super(identifier, parent, 2);
        this.currentPage = 0;
        this.slots = slots;
        this.iconsToPlace = new LinkedList<>();
    }

    @Override
    public void fillElement(final BaseGui gui) {
        iconsToPlace.clear(); // Cleared here so that next-page() can count how many leftover icons there are
        super.fillElement(gui);
        placeIcons(gui);
    }

    public void queueIconToPlace(final ItemStack icon) {
        iconsToPlace.add(icon);
    }

    public void placeIcons(final BaseGui gui) {
        final Inventory inventory = gui.getInventory();
        for (int i = 0; i < slots.length; i++) {
            final int j = i + currentPage * slots.length;
            if (j < iconsToPlace.size()) {
                final ItemStack stack = iconsToPlace.get(j);
                inventory.setItem(slots[i], stack);
            }
        }
    }

    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
        }
    }

    public boolean hasNextPage() {
        return (currentPage + 1) * slots.length < iconsToPlace.size();
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
        }
    }

    @Override
    public Class<Page> getThisClass() {
        return Page.class;
    }
}
