package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Element;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
        iconsToPlace.clear(); // Cleared here so that nextpage() can count how many leftover icons there are
        super.fillElement(gui);
        placeIcons(gui);
    }

    public void queueIconToPlace(final ItemStack icon) {
        iconsToPlace.add(icon);
    }

    public void placeIcons(final BaseGui gui) {
        final Inventory inventory = gui.getInventory();
        final List<ItemStack> icons = new ArrayList<>(iconsToPlace);
        for (int i = currentPage * slots.length; i < iconsToPlace.size(); i++) {
            final ItemStack stack = icons.get(i);
            inventory.setItem(slots[i], stack);
            iconsToPlace.remove(stack);
        }
    }

    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
        }
    }

    public boolean hasNextPage() {
        return iconsToPlace.size() > 0;
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
