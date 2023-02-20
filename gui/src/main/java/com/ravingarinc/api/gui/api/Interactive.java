package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Supplier;

public interface Interactive extends Actionable, Component {
    <T, Z> void setMeta(PersistentDataType<T, Z> type, String key, Z value);

    <T, Z> Z getMeta(PersistentDataType<T, Z> type, String key);

    <T, Z> boolean hasMeta(PersistentDataType<T, Z> type, String key);

    boolean handleClickedItem(BaseGui gui, InventoryClickEvent event);

    ItemStack getItem();

    void setItem(ItemStack item);

    void addChild(Supplier<Component> component);
}
