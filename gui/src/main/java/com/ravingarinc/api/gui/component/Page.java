package com.ravingarinc.api.gui.component;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Element;
import com.ravingarinc.api.gui.component.icon.PageIcon;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Page extends Element {
    public static final Page PAGE = new Page("PAGE", null);
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

    public void addPageIcon(final String identifier, final String display, final String lore, final Material material, final Predicate<BaseGui> predicate) {
        addChild(() -> new PageIcon(identifier, display, lore, this.identifier, material, predicate, (item) -> {
        }));
    }

    public void queueIconToPlace(final ItemStack icon) {
        iconsToPlace.add(icon);
    }

    public void placeIcons(final BaseGui gui) {
        final Inventory inventory = gui.getInventory();
        for (int i = currentPage * slots.length; i < iconsToPlace.size(); i++) {
            inventory.setItem(slots[i], iconsToPlace.get(i));
        }
    }

    public void nextPage() {
        if (iconsToPlace.size() > 0) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (currentPage != 0) {
            currentPage--;
        }
    }

    @Override
    public Class<Page> getThisClass() {
        return Page.class;
    }
}
