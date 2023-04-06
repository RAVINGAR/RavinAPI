package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.builder.GuiProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Interactive extends Actionable, Component {

    boolean handleClickedItem(BaseGui gui, InventoryClickEvent event);

    ItemStack getItem();

    default <T, Z> void setMeta(final PersistentDataType<T, Z> type, final String key, final Z value) {
        final ItemStack item = getItem();
        if (item == null) {
            return;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(GuiProvider.getKey(key), type, value);
        item.setItemMeta(meta);
    }

    default <T, Z> Z getMeta(final PersistentDataType<T, Z> type, final String key) {
        final ItemStack item = getItem();
        if (item == null) {
            return null;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(GuiProvider.getKey(key), type);
    }

    default <T, Z> boolean hasMeta(final PersistentDataType<T, Z> type, final String key) {
        return getMeta(type, key) != null;
    }

    default void updateItem(@Nullable final String name, @Nullable final String lore, @Nullable final Material material) {
        final ItemStack item = getItem();
        if (item == null) {
            return;
        }
        if (material != null && !material.isAir()) {
            item.setType(material);
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (name != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        if (lore != null) {
            meta.setLore(new ArrayList<>(Arrays.asList((ChatColor.translateAlternateColorCodes('&', lore)).split("\n"))));
        }
        item.setItemMeta(meta);
    }

    default void updateMeta(final Consumer<ItemMeta> consumer) {
        final ItemStack item = getItem();
        final ItemMeta meta = item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);
    }

    void addAmount(int delta);

    void addChild(Supplier<Component> component);
}
