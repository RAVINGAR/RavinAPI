package com.ravingarinc.api.gui.component.observer;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.api.Interactive;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class ItemObserver extends Observer {
    private final Predicate<ItemStack> predicate;

    public ItemObserver(final Interactive parent, final Predicate<ItemStack> predicate) {
        super(parent);
        this.predicate = predicate;
    }

    @Override
    public void fillElement(final BaseGui gui, Player player) {
        if (predicate.test(((Interactive) this.parent).getItem())) {
            performAllActions(gui, player);
            gui.queueRefresh();
        }
    }
}
