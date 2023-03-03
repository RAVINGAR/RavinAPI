package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public interface Interactive extends Actionable, Component {

    boolean handleClickedItem(BaseGui gui, InventoryClickEvent event);

    ItemStack getItem();

    void setItem(ItemStack item);

    void addChild(Supplier<Component> component);
}
