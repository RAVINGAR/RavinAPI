package com.ravingarinc.api.gui.component.icon;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class ItemObserver extends Observer {
    private final Predicate<ItemStack> predicate;

    public ItemObserver(final Interactive parent, final Predicate<ItemStack> predicate) {
        super(parent);
        this.predicate = predicate;
    }

    @Override
    public void fillElement(final BaseGui gui) {
        if (predicate.test(((Interactive) this.parent).getItem())) {
            performAllActions(gui);
        }
    }
}
